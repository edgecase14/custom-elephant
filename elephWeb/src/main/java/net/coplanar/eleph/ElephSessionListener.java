package net.coplanar.eleph;

import java.util.HashMap;

import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

@WebListener
public class ElephSessionListener implements HttpSessionListener {

    private static HashMap<String, HttpSession> hsessions;
	private static int totalActiveSessions;  // oops, not atomic, and not interceptable
	
  public static int getTotalActiveSession(){
	return totalActiveSessions;
  }
	
  public ElephSessionListener( ) {
	  super();
      hsessions = new HashMap<String, HttpSession>();
	  System.out.println("ElephSessionListener Created.");
}
  
  @Override
  public void sessionCreated(HttpSessionEvent se) {
	totalActiveSessions++;
	System.out.println("ElephSessionCreated - add one session into counter");
    HttpSession session = se.getSession();
    String upn = (String) session.getAttribute("theUPN");
    String sid = session.getId();
    System.out.println("session id: " + sid + " UPN: " + upn);

    // Add keys and values (Country, City)
    hsessions.put(sid, session);

  }

  @Override
  public void sessionDestroyed(HttpSessionEvent se) {
	totalActiveSessions--;
    HttpSession session = se.getSession();
    String sid = session.getId();
    System.out.println("session id: " + sid);
    hsessions.remove(sid);
	System.out.println("ElephSessionDestroyed - deduct one session from counter");
  }
  
  // need an observer
  public static HashMap<String, HttpSession> getSessions() {
	  return hsessions;
  }
}