package net.coplanar.eleph;

import java.io.IOException;

import javax.websocket.EncodeException;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.PathParam;
import java.util.List;
import javax.json.JsonObjectBuilder;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
//import javax.json.JsonArray;
import javax.json.Json;

import net.coplanar.beanz.TsCellz;
import net.coplanar.ents.TsCell;

import javax.ejb.EJB;

/**
 * Servlet implementation class Tsc
 */
@ServerEndpoint(value = "/Tsc/{userid}",
    encoders= { MessageEncoder.class },
    decoders= { MessageDecoder.class }
)
public class Tsc  {
       
	@EJB(lookup="java:app/eleph-brain/TsCellz!net.coplanar.beanz.TsCellz") TsCellz tscell;
	
    @OnMessage
    public void dispatch(Message message, Session session) throws IOException, EncodeException {
    	//String id = (String) session.getUserProperties().get("USER_ID");
        //System.out.println("Say hello to '" + id + "'");
    	switch (message.getType()) {
    	case "cell-list":
    		this.cellList(session);
    		break;
    	case "cell-update":
    		this.cellUpdate(session, message.getPayload());
    		break;
    	default:
    		System.out.println("dispatch: unknown message type");
    	}
        return;
    }

    private void cellList(Session session) throws IOException, EncodeException {
    	setBuffered(session);
    	    	
        List<TsCell> mylist = tscell.getAllTsCells();

        for (TsCell acell : mylist ) {
                JsonObjectBuilder builder = Json.createObjectBuilder();

        	builder.add("cellid", acell.getId());
 
        	JsonObjectBuilder plo = Json.createObjectBuilder()
        			.add("type", "cell-list")
        			.add("payload", builder);

        	push(session, plo);
        	// now send corresponding cellUpdate - moves MVC controller out of browser
        	// must remove send(cell-update) from tsc.js also
        }
        sendToSession(session);

    }
    
    private void cellUpdate(Session session, JsonObject obj) throws IOException, EncodeException {
    	Message response = new Message();
    	response.setType("cell-update");

    	int id = obj.getInt("id");
    	
    	TsCell mytscell = tscell.getTsCell(id);
    	
    	JsonObjectBuilder builder = Json.createObjectBuilder();
    	builder.add("id", mytscell.getId());
    	if ( obj.containsKey("contents") ) {
    		builder.add("ack", true);
    		tscell.updateTsCellEntry(id, Float.parseFloat(obj.getString("contents")));
    		System.out.println("contents: " + Float.parseFloat(obj.getString("contents")));
    		tscell.flush();
    	} else {
    		builder.add("contents", mytscell.getEntry());
    	}
    	JsonObject cellJson = builder.build();
    	response.setPayload(cellJson);
    	
    	session.getBasicRemote().sendObject(response);
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
    public void helloOnOpen(@PathParam("userid") String id, Session session) {
    	session.getUserProperties().put("USER_ID", id);
    	session.getUserProperties().put("IS_BUFFERED", 0);
        System.out.println("WebSocket opened: " + session.getId() + " userid: " + id);
    }

    @OnClose
    public void helloOnClose(CloseReason reason) {
        System.out.println("WebSocket connection closed with CloseCode: " + reason.getCloseCode());
    }

}
