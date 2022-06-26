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

import net.coplanar.beanz.TsCellBean;
import net.coplanar.ents.TsCell;
import net.coplanar.beanz.TsUserBean;
import net.coplanar.ents.TsUser;
import net.coplanar.beanz.ProjectBean;
import net.coplanar.ents.Project;
import net.coplanar.beanz.UserProjectBean;
import net.coplanar.ents.UserProject;

import javax.ejb.EJB;

/**
 * Servlet implementation class Tsc
 */
@ServerEndpoint(value = "/Tsc/{userid}",
    encoders= { MessageEncoder.class },
    decoders= { MessageDecoder.class }
)
public class Tsc  {
       
	@EJB(lookup="java:app/eleph-brain/TsCellBean!net.coplanar.beanz.TsCellBean") TsCellBean tscell;
	@EJB(lookup="java:app/eleph-brain/TsUserBean!net.coplanar.beanz.TsUserBean") TsUserBean tsuser;
	@EJB(lookup="java:app/eleph-brain/ProjectBean!net.coplanar.beanz.ProjectBean") ProjectBean project;
	@EJB(lookup="java:app/eleph-brain/UserProjectBean!net.coplanar.beanz.UserProjectBean") UserProjectBean userProject;
	
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
    	    	
    	String id = (String) session.getUserProperties().get("USER_ID");
//    	TsUser tsu = tsuser.getUserFromUsername(id);
    	TsUser tsu = tsuser.getUser(1);
    	List<UserProject> prjs = tsu.getProjects();
    	for (UserProject userProj : prjs) {
    	  Project prj = userProj.getProject();
          JsonObjectBuilder pbuilder = Json.createObjectBuilder();

           pbuilder.add("projid", prj.getProj_id());
           pbuilder.add("job_id", prj.getJobId());
           pbuilder.add("pname", prj.getPname());
 
          JsonObjectBuilder pplo = Json.createObjectBuilder()
        			.add("type", "row-list")
        			.add("payload", pbuilder);
       	  push(session, pplo);
    		
          List<TsCell> mylist = tscell.getAllTsCells(tsu, prj);

          for (TsCell acell : mylist ) {
            JsonObjectBuilder builder = Json.createObjectBuilder();

        	builder.add("projid", acell.getProject().getProj_id())
			.add("date", acell.getDate().toString());
        	builder.add("cellid", acell.getId());
 
        	JsonObjectBuilder plo = Json.createObjectBuilder()
        			.add("type", "cell-list")
        			.add("payload", builder);
        	push(session, plo);
        	// TODO: subscribe to changes of cell, ensuring no race between subscribe
        	// and sending of initial value
            JsonObjectBuilder builder2 = Json.createObjectBuilder()
            		.add("id", acell.getId())
                    .add("action", "init")
            		.add("date", acell.getDate().toString())
            		.add("contents", acell.getEntry())
            		.add("note",  acell.getNote());
  
         	JsonObjectBuilder plo2 = Json.createObjectBuilder()
         			.add("type", "cell-update")
         			.add("payload", builder2);
         	push(session, plo2);

          }
    	}
        JsonObjectBuilder builder = Json.createObjectBuilder();

    	builder.add("user", (String) session.getUserProperties().get("USER_ID"));

    	JsonObjectBuilder plo = Json.createObjectBuilder()
    			.add("type", "username")
    			.add("payload", builder);
    	push(session, plo);

    	sendToSession(session);

    }
    
    private void cellUpdate(Session session, JsonObject obj) throws IOException, EncodeException {
    	Message response = new Message();
    	response.setType("cell-update");

    	int id = obj.getInt("id");
    	
    	TsCell mytscell = tscell.getTsCell(id);
    	
    	JsonObjectBuilder builder = Json.createObjectBuilder();
    	builder.add("id", mytscell.getId());
    		builder.add("action", "ack");
    		tscell.updateTsCellEntry(id, Float.parseFloat(obj.getString("contents")), obj.getString("note"));
    		System.out.println("contents: " + Float.parseFloat(obj.getString("contents")));
    		tscell.flush();
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
