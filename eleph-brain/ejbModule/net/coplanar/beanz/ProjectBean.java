package net.coplanar.beanz;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import net.coplanar.ents.Project;

/**
 * Session Bean implementation class Projects
 */
@Stateless
public class ProjectBean {

    @PersistenceContext private EntityManager em;

    /**
     * Default constructor. 
     */
    public ProjectBean() {
        // TODO Auto-generated constructor stub
    }
    public Project getProject(int proj_id ) {
    	Project project = em.find(Project.class, proj_id);
    	return project;
    }

}
