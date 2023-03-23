package net.coplanar.beanz;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

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
