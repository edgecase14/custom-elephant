package net.coplanar.beanz;

import java.time.LocalDate;
import java.util.List;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import net.coplanar.ents.StatDay;

/**
 * Session Bean implementation class StatDayBean
 */
@Stateless
public class StatDayBean {

    @PersistenceContext private EntityManager em;

   /**
     * Default constructor. 
     */
    public StatDayBean() {
        // TODO Auto-generated constructor stub
    }
    public StatDay getStatDay(int holiday_id ) {
    	StatDay stat_day = em.find(StatDay.class, holiday_id);
    	return stat_day;
    }
    public List<StatDay> getStatDays(LocalDate start_date, LocalDate end_date) {
        TypedQuery<StatDay> query = em.createQuery(
            "SELECT sd FROM StatDay sd WHERE holiday >= :start AND holiday <= :end", StatDay.class)
        		.setParameter("start", start_date)
        		.setParameter("end", end_date);
        return query.getResultList();
    }
    

}
