package net.coplanar.eleph;

import java.io.IOException;

import jakarta.websocket.EncodeException;
import jakarta.websocket.CloseReason;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import jakarta.websocket.server.PathParam;

import java.util.HashMap;
import java.util.List;
import java.time.LocalDate;
import jakarta.json.JsonObjectBuilder;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.UserTransaction;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
//import javax.json.JsonArray;
import jakarta.json.Json;

import net.coplanar.beanz.*;
import net.coplanar.ents.*;
import jakarta.annotation.Resource;
import jakarta.ejb.EJB;

/**
 * Servlet implementation class SessionAdmin
 */
@ServerEndpoint(value="/SessionAdmin",
	    encoders= { MessageEncoder.class },
	    decoders= { MessageDecoder.class },
	    // this is where to check authentication, and deny connection synchronously before onOpen or onMessage which are both async it seems
	    configurator = WsAuth.class
)
// how much of Tsc.class could go in a common superclass?
public class SessionAdmin {
	@EJB(lookup="java:app/eleph-brain/TsUserBean!net.coplanar.beanz.TsUserBean") TsUserBean tsuser;
	private HttpSession theHttpSession;

//	ElephSessionListener listener = new ElephSessionListener(); // is it a singleton?
	
    @OnMessage
    public void dispatch(Message message, Session session) throws IOException, EncodeException {
    	// what happens if socket is closed when we get here?  race? check isOpen?
    	switch (message.getType()) {
    	case "session-list":
    		this.sessionList(session);
    		break;
    	case "close-session":
    		this.closeSession(session, message.getPayload());
    		break;
    	default:
    		System.out.println("dispatch: unknown message type");
    	}
        return;
    }

    private void sessionList(Session session) throws IOException, EncodeException {
    	setBuffered(session);

    	
    	// can we just get sesssion list from container, and pull out the Attribute "theUPN" that we stashed in there?
    	// sigh, seems not - let's see if our private session map works
    	HashMap<String, HttpSession> hsessions = ElephSessionListener.getSessions();
    	
    	hsessions.forEach(
    	            (sid, sess)
    	                -> {
    	                	String upn = (String) sess.getAttribute("theUPN");
    	                	System.out.println("upn: " + upn + " sid: " + sid);
    	            JsonObjectBuilder builder = Json.createObjectBuilder();

    	        	builder.add("sess-id", sid)
    				.add("upn", upn);
    	 
    	        	JsonObjectBuilder plo = Json.createObjectBuilder()
    	        			.add("type", "sess-list")
    	        			.add("payload", builder);
    	        	push(session, plo);
    	            }
    	);

    	sendToSession(session);
  	
    }
    
    private void sendToSession(Session session) throws EncodeException, IOException {
  	  int isBuffered = (int) session.getUserProperties().get("IS_BUFFERED");
            if (isBuffered == 0) {
          	  System.out.println("must be buffered!");
                //session.getBasicRemote().sendObject(response);
            } else {
          	  JsonArrayBuilder buf = (JsonArrayBuilder) session.getUserProperties().get("JSON_OBJ");
                session.getBasicRemote().sendText(buf.build().toString());
            }
      }

      private void setBuffered(Session session) {
      	session.getUserProperties().put("IS_BUFFERED", 1);
          session.getUserProperties().put("JSON_OBJ" , Json.createArrayBuilder());
      }


    private void push(Session session, JsonObjectBuilder jso) {
        JsonArrayBuilder buf = (JsonArrayBuilder) session.getUserProperties().get("JSON_OBJ");
 	   buf.add(jso);
     }

    @OnOpen
    // what prevents 2 connections to same endpoint, for 1 client (http session) ?
    // *can* it throw anything??
    public void helloOnOpen(@PathParam("userid") String id, Session session) throws IOException {
    	// later use "userid" to edit someone else's timesheet, if allowed eg supervisor

    	 theHttpSession = (HttpSession)session.getUserProperties().get("theHttpSession");
         System.out.println("WebSocket sessION ID: " + theHttpSession.getId());
    	
    	// most of this belongs in ServerEndpointConfig
    	String upn = (String) session.getUserPrincipal().getName();
        System.out.println("WebSocket opened for UPN: " + upn);
        // these 2 should probably be done with an exception try block 
        if (upn == null) {
            System.out.println("WebSocket closed due to NULL UPN");
            CloseReason cr = new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Authentication missing");
            session.close(cr);
            return;
    	}
    	TsUser tsu = tsuser.getUserFromUsername(upn);
    	if (tsu == null) {
            System.out.println("WebSocket closed: UPN lookup failure: " + upn);
            CloseReason cr = new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Authentication error");
            session.close(cr);
            return;
    	}

    	String uid = tsu.getUsername();
    	int user_id = tsu.getUser_id();
    	session.getUserProperties().put("TSUSER", user_id);
    	
        // setup observers here - how to synchronize before "cell-list"
    	session.getUserProperties().put("IS_BUFFERED", 0);
        System.out.println("WebSocket opened for uid: " + uid);
        // reconnect logic - if new session, send json message, to trigger "cell-list"

    }
   
    private void closeSession(Session session, JsonObject obj) throws IOException, EncodeException {
    	Message response = new Message();
    	response.setType("session-closed");
    }
}
