package net.coplanar.beanz;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Session Bean implementation class UserProjectBean
 */
@Stateless
public class UserProjectBean {
    @PersistenceContext private EntityManager em;

    /**
     * Default constructor. 
     */
    public UserProjectBean() {
        // TODO Auto-generated constructor stub
    }
}
