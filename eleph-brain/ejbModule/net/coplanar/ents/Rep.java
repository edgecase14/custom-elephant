package net.coplanar.ents;

import java.io.Serializable;
import java.lang.String;
import javax.persistence.*;

/**
 * Entity implementation class for Entity: Rep
 *
 */
@Entity

public class Rep implements Serializable {

	   
	@Id
	private int rep_id;
	@Column(nullable=false)
	private String rep_name;
	private static final long serialVersionUID = 1L;

	public Rep() {
		super();
	}   
	public int getRep_id() {
		return this.rep_id;
	}

	public void setRep_id(int rep_id) {
		this.rep_id = rep_id;
	}   
	public String getRep_name() {
		return this.rep_name;
	}

	public void setRep_name(String rep_name) {
		this.rep_name = rep_name;
	}
   
}
