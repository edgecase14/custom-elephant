// https://stackoverflow.com/questions/20240591/websocket-httpsession-returns-null

// this is a fix for what I thought was default behavior: on "new" websocket
//   connection, create an HttpSession and set JSESSIONID cookie in response

package net.coplanar.eleph;


import jakarta.servlet.ServletRequestEvent;
import jakarta.servlet.ServletRequestListener;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@WebListener
public class RequestListener implements ServletRequestListener {

    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        // TODO Auto-generated method stub

    }

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
    	System.out.println("--MYrequestInitialized --");
    	// can we skip this if no UPN - unauthenticated?
    	// seems like an oversight in the Websocket JSR spec that I have to do this
        HttpSession ms = ((HttpServletRequest) sre.getServletRequest()).getSession();
    }

}