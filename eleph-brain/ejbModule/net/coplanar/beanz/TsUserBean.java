package net.coplanar.beanz;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import jakarta.persistence.PersistenceContext;

import net.coplanar.ents.*;

/**
 * Session Bean implementation class UserBean
 */
@Stateless
public class TsUserBean {

    @PersistenceContext private EntityManager em;
    
    /**
     * Default constructor. 
     */
    public TsUserBean() {
        // TODO Auto-generated constructor stub
    }
    public TsUser getUser(int user_id ) {
    	TsUser user = em.find(TsUser.class, user_id);
    	return user;
    }
    public TsUser getUserFromUsername(String username) {
    	// thanks to:
    	// https://vladmihalcea.com/the-best-way-to-map-a-naturalid-business-key-with-jpa-and-hibernate/
    	TsUser user = em.unwrap(Session.class)
    			.bySimpleNaturalId(TsUser.class)
    			.load(username);
    	return user;
    }
}
