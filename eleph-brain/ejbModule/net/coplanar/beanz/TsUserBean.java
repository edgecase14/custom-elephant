package net.coplanar.beanz;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import net.coplanar.ents.TsUser;

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
    	TsUser user = em.find(TsUser.class, username);
    	return user;
    }

}
