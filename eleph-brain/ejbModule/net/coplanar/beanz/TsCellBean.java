package net.coplanar.beanz;

import jakarta.ejb.Stateful;
//import javax.ejb.LocalBean;
import java.util.List;
import java.time.LocalDate;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
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
            "SELECT tsc FROM TsCell tsc WHERE user_id = :user_id AND proj_id = :proj_id", TsCell.class)
        		.setParameter("user_id", user_id)
        		.setParameter("proj_id", proj_id);
        return query.getResultList();
    }
    public List<TsCell> getAllTsCells(TsUser tsuser, Project proj, LocalDate start_date, LocalDate end_date) {
    	int user_id = tsuser.getUser_id();
    	int proj_id = proj.getProj_id();
        TypedQuery<TsCell> query = em.createQuery(
            "SELECT tsc FROM TsCell tsc WHERE user_id = :user_id AND proj_id = :proj_id AND te_date >= :start AND te_date <= :end", TsCell.class)
        		.setParameter("user_id", user_id)
        		.setParameter("proj_id", proj_id)
        		.setParameter("start", start_date)
        		.setParameter("end", end_date);
        return query.getResultList();
    }
    
    // can we use an SQL array to get the cells AND time total in 1 query?
    public Float getTsCellsTotal(TsUser tsuser, LocalDate start_date, LocalDate end_date) {
    	int user_id = tsuser.getUser_id();
        Float hours_total = em.createQuery(
            "SELECT CAST(SUM(entry) AS float) AS hours_total FROM TsCell WHERE user_id = :user_id AND te_date >= :start AND te_date <= :end", Float.class)
        		.setParameter("user_id", user_id)
        		.setParameter("start", start_date)
        		.setParameter("end", end_date).getSingleResult(); // TODO handle empty result
       return hours_total;
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