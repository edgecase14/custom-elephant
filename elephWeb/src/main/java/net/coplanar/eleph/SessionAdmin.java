package net.coplanar.eleph;

import java.io.IOException;

import jakarta.websocket.EncodeException;
import jakarta.websocket.OnMessage;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

import java.util.HashMap;
import jakarta.json.JsonObjectBuilder;
import jakarta.servlet.http.HttpSession;
//import javax.json.JsonArray;
import jakarta.json.Json;

import jakarta.inject.Inject;

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
public class SessionAdmin extends Endpoint {
// can this be injected also?
	private @Inject ElephSessionListener sl;

//	ElephSessionListener listener = new ElephSessionListener(); // is it a singleton?
	
    @OnMessage
    public void dispatch(Message message, Session session) throws IOException, EncodeException {
    	// what happens if socket is closed when we get here?  race? check isOpen?

    	// we need some sort of loader that adds a list of sub-controller classes to a map.
    	// and push this up to superclass
    	switch (message.getType()) {
    	case "session-list":
    		this.sessionList(session);
    		break;
//    	case "close-session":
//    		this.closeSession(session, message.getPayload());
//    		break;
    	default:
    		System.out.println("dispatch: unknown message type");
    	}
        return;
    }

    private void sessionList(Session session) throws IOException, EncodeException {
    	setBuffered(session);

    	
    	// can we just get sesssion list from container, and pull out the Attribute "theUPN" that we stashed in there?
    	// sigh, seems not - let's see if our private session map works
    	HashMap<String, HttpSession> hsessions = sl.getSessions();
    	
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
    
}
