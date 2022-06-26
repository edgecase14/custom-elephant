package net.coplanar.beanz;

import javax.ejb.Stateful;
//import javax.ejb.LocalBean;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import net.coplanar.ents.TsCell;
import net.coplanar.ents.TsUser;
import net.coplanar.ents.Project;

@Stateful
public class TsCellBean {
    // Injected database connection:
    @PersistenceContext private EntityManager em;
 
    // Stores a new guest:
    public void persist(TsCell tscell) {
        em.persist(tscell);
    }
    public void flush() {
    	em.flush();
    }

    public List<TsCell> getAllTsCells(TsUser tsuser, Project proj) {
    	int user_id = tsuser.getUser_id();
    	int proj_id = proj.getProj_id();
        TypedQuery<TsCell> query = em.createQuery(
            "SELECT tsc FROM TsCell tsc WHERE user_id = :user_id AND proj_id = :proj_id ORDER BY tsc.id", TsCell.class)
        		.setParameter("user_id", user_id)
        		.setParameter("proj_id", proj_id);
        return query.getResultList();
    }
    public TsCell getTsCell(int id ) {
    	TsCell thecell = em.find(TsCell.class, id);
    	return thecell;
    }
    public void updateTsCellEntry (int id, float entry, String note) {
    	//em.getTransaction().begin();  // can't while using container managed transations
    	TsCell mytscell = getTsCell(id);
    	mytscell.setEntry(entry);
    	mytscell.setNote(note);
    	//em.getTransaction().begin();
    	
    }
}