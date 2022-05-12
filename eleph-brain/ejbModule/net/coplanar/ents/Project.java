package net.coplanar.ents;

import java.io.Serializable;
import java.lang.String;
import javax.persistence.*;

/**
 * Entity implementation class for Entity: Projects
 *
 */
@Entity

public class Project implements Serializable {

	   
	@Id
	@GeneratedValue ( strategy = GenerationType.IDENTITY )
	private int proj_id;
	private String pname;
	private static final long serialVersionUID = 1L;

	public Project() {
		super();
	}   
	public int getProj_id() {
		return this.proj_id;
	}

	public void setProj_id(int proj_id) {
		this.proj_id = proj_id;
	}   
	public String getPname() {
		return this.pname;
	}

	public void setPname(String pname) {
		this.pname = pname;
	}
   
}
