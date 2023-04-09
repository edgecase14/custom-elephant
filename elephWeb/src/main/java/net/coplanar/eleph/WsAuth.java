package net.coplanar.eleph;

import java.util.ArrayList;

import jakarta.servlet.http.HttpSession;
import jakarta.websocket.HandshakeResponse;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.websocket.server.ServerEndpointConfig;

public class WsAuth extends ServerEndpointConfig.Configurator{

@Override
public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
	//sec.getUserProperties().get(thing, request.getUserPrincipal());
	
    super.modifyHandshake(sec, request, response);
	String upn = (String) request.getUserPrincipal().getName();
    System.out.println("modifyHandshake UPN: " + upn);
    if (upn == null) {
    	// debatable whether this is by-the-book or not
    	response.getHeaders().put(HandshakeResponse.SEC_WEBSOCKET_ACCEPT, new ArrayList<String>());
        System.out.println("modifyHandshake null UPN");
    }
    // https://stackoverflow.com/questions/17936440/accessing-httpsession-from-httpservletrequest-in-a-web-socket-serverendpoint
    HttpSession httpSession = (HttpSession)request.getHttpSession();
//    sec.getUserProperties().put(HttpSession.class.getName(),httpSession);
    sec.getUserProperties().put("theHttpSession",httpSession);
    // this is likely gettable somewhere already
    httpSession.setAttribute("theUPN",upn);
    
    // fork a thread to preload user - investigate using Futures to do this
    // maybe start a JPA Extended Transaction on new httpSession?
}
}

// how to asynchronously get user from database... promise resolved in first onMessage?
// https://stackoverflow.com/questions/17936440/accessing-httpsession-from-httpservletrequest-in-a-web-socket-serverendpoint
// https://stackoverflow.com/questions/20240591/websocket-httpsession-returns-null