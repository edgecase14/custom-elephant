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
    // fork a thread to preload user
}
}

// how to asynchronously get user from database... promise resolved in first onMessage?