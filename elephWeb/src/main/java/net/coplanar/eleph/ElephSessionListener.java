package net.coplanar.eleph;

import java.util.HashMap;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

@WebListener
@ApplicationScoped
public class ElephSessionListener implements HttpSessionListener {

    private HashMap<String, HttpSession> hsessions;
	
  public ElephSessionListener( ) {
	  super();
      hsessions = new HashMap<String, HttpSession>();
	  System.out.println("ElephSessionListener Created.");
}
  
  @Override
  public void sessionCreated(HttpSessionEvent se) {
    HttpSession session = se.getSession();
    String upn = (String) session.getAttribute("theUPN");
    String sid = session.getId();
    System.out.println("add session id: " + sid + " UPN: " + upn);

    hsessions.put(sid, session);

  }

  @Override
  public void sessionDestroyed(HttpSessionEvent se) {
    HttpSession session = se.getSession();
    String sid = session.getId();
    System.out.println("remove session id: " + sid);
    hsessions.remove(sid);
  }
  
  // need an observer
  public HashMap<String, HttpSession> getSessions() {
	  return hsessions;
  }
}