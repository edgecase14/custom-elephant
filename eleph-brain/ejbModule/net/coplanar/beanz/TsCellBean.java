package net.coplanar.beanz;

import javax.ejb.Stateful;
//import javax.ejb.LocalBean;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import net.coplanar.ents.TsCell;

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

    // Retrieves all the guests:
    public List<TsCell> getAllTsCells() {
        TypedQuery<TsCell> query = em.createQuery(
            "SELECT tsc FROM TsCell tsc ORDER BY tsc.id", TsCell.class);
        return query.getResultList();
    }
    public TsCell getTsCell(int id ) {
    	TsCell thecell = em.find(TsCell.class, id);
    	return thecell;
    }
    public void updateTsCellEntry (int id, float entry) {
    	//em.getTransaction().begin();  // can't while using container managed transations
    	TsCell mytscell = getTsCell(id);
    	mytscell.setEntry(entry);
    	//em.getTransaction().begin();
    	
    }
}