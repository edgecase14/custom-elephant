package net.coplanar.eleph;

import java.io.IOException;

import jakarta.ejb.EJB;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.CloseReason;
import jakarta.websocket.EncodeException;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import net.coplanar.beanz.TsUserBean;
import net.coplanar.ents.TsUser;

public class Endpoint  {
	@EJB(lookup="java:app/eleph-brain/TsUserBean!net.coplanar.beanz.TsUserBean") TsUserBean tsuser;
	// inject didn't work - no session context active in helloOnOpen
	private HttpSession theHttpSession;

	public void sendToSession(Session session) throws EncodeException, IOException {
		int isBuffered = (int) session.getUserProperties().get("IS_BUFFERED");
		if (isBuffered == 0) {
			System.out.println("must be buffered!");
			//session.getBasicRemote().sendObject(response);
		} else {
			JsonArrayBuilder buf = (JsonArrayBuilder) session.getUserProperties().get("JSON_OBJ");
			session.getBasicRemote().sendText(buf.build().toString());
		}
	}

	public void setBuffered(Session session) {
		session.getUserProperties().put("IS_BUFFERED", 1);
		session.getUserProperties().put("JSON_OBJ" , Json.createArrayBuilder());
	}

	public void push(Session session, JsonObjectBuilder jso) {
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

	// this might race with Websocket's HttpSessionListener sessonDestroyed
	@OnClose
	public void helloOnClose(CloseReason reason) {
		System.out.println("WebSocket connection closed with CloseCode: " + reason.getCloseCode());
		// reconnect logic - if closed due to keepalive timeout, keep httpSession
	}
	// ideas for @onError() and other exception handling:
	// https://github.com/martinandersson/websocket-exception-handling/blob/master/src/main/java/martinandersson/com/websockets/MyServerEndpoint.java
}