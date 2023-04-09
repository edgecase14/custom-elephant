package net.coplanar.eleph;

import java.io.IOException;

import jakarta.websocket.EncodeException;
import jakarta.websocket.OnMessage;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import java.util.List;
import java.time.LocalDate;
import jakarta.json.JsonObjectBuilder;
import jakarta.transaction.UserTransaction;
import jakarta.json.JsonObject;
import jakarta.json.Json;

import net.coplanar.beanz.*;
import net.coplanar.ents.*;
import jakarta.annotation.Resource;
import jakarta.ejb.EJB;

// TODO - rename this TimesheetController (ala MVC)
// TODO - decouple Json building and WebSocket message boundaries ( and when asynch, priorities) in case implementation changes

/**
 * Servlet implementation class Tsc
 */
@ServerEndpoint(value = "/Tsc/{userid}",
    encoders= { MessageEncoder.class },
    decoders= { MessageDecoder.class },
    // this is where to check authentication, and deny connection synchronously before onOpen or onMessage which are both async it seems
    configurator = WsAuth.class
)
public class Tsc  extends Endpoint {
       
	@EJB(lookup="java:app/eleph-brain/StatDayBean!net.coplanar.beanz.StatDayBean") StatDayBean statDay;
	@EJB(lookup="java:app/eleph-brain/TsCellBean!net.coplanar.beanz.TsCellBean") TsCellBean tscell;
//	@EJB(lookup="java:app/eleph-brain/TsUserBean!net.coplanar.beanz.TsUserBean") TsUserBean tsuser;
	@EJB(lookup="java:app/eleph-brain/ProjectBean!net.coplanar.beanz.ProjectBean") ProjectBean project;
	@EJB(lookup="java:app/eleph-brain/UserProjectBean!net.coplanar.beanz.UserProjectBean") UserProjectBean userProject;
	@Resource private UserTransaction utx;
	
    @OnMessage
    public void dispatch(Message message, Session session) throws IOException, EncodeException {
    	// what happens of we *do* throw an exception?  close socket?
    	// what happens if socket is closed when we get here?  race? check isOpen?

    	// TODO - add a "compoment" decode first, maybe with "controller" case going to existing one
    	// how would access control be handled them?  will MVC controller "filter" before dispatching?
    	// that could be required by either business rules ACL, or state-machine, separate filter chains
    	// for each?
    	// this may push DOM object/event models into Java?
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
    	try {
    		utx.begin();
    	} catch (Exception e) {
    		throw new IOException(e);
    	}    	
    	
    	List<StatDay> sds = statDay.getStatDays(LocalDate.parse("2022-06-01"), LocalDate.parse("2022-07-30"));
    	for (StatDay sd : sds) {
    		JsonObjectBuilder pbuilder = Json.createObjectBuilder();

    		pbuilder.add("holiday", sd.getHolidayDate().toString());
            pbuilder.add("holiday_name", sd.getHolidayName());
  
           JsonObjectBuilder pplo = Json.createObjectBuilder()
         			.add("type", "stat-days")
         			.add("payload", pbuilder);
           push(session, pplo);
    	}
    	
//    	String id = (String) session.getUserPrincipal().getName();
//    	String id = "jjackson";
    	int user_id = (int)session.getUserProperties().get("TSUSER");
    	TsUser tsu = tsuser.getUser(user_id);

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
    	builder.add("user", (String) session.getUserPrincipal().getName());
    	JsonObjectBuilder plo = Json.createObjectBuilder()
    			.add("type", "username")
    			.add("payload", builder);
    	push(session, plo);

    	Float total =  tscell.getTsCellsTotal(tsu, LocalDate.parse("2022-05-01"), LocalDate.parse("2022-06-30"));
        JsonObjectBuilder tbuilder = Json.createObjectBuilder();
    	tbuilder.add("total", total.toString());
    	JsonObjectBuilder tot = Json.createObjectBuilder()
    			.add("type", "calc-update")
    			.add("payload", tbuilder);
    	push(session, tot);

    	sendToSession(session);
    	try {
    		utx.commit(); // or flush if Extended Transaction
    	} catch (Exception e) {
    		throw new IOException(e);
    	} 
    }
    
    // try moving this to a sub-package of HTML Custom Element backends, with added "compoment": "ts-cell" to messages
	// should also be wrapped in utx?
    private void cellUpdate(Session session, JsonObject obj) throws IOException, EncodeException {
    	Message response = new Message();
    	response.setType("cell-update");

    	int id = obj.getInt("id");
    	
    	TsCell mytscell = tscell.getTsCell(id);
    	
    	JsonObjectBuilder builder = Json.createObjectBuilder();
    	builder.add("id", mytscell.getId());
    		builder.add("action", "ack");
    		// how can concurrent updates be handled?  only 1 session in "update mode" at a time?
    		tscell.updateTsCellEntry(id, Float.parseFloat(obj.getString("contents")), obj.getString("note"));
    		System.out.println("contents: " + Float.parseFloat(obj.getString("contents")));
    		// this is a bit suspicious
    		//tscell.flush();
    	   	JsonObject cellJson = builder.build();
    	response.setPayload(cellJson);
    	
    	session.getBasicRemote().sendObject(response);
    }

}