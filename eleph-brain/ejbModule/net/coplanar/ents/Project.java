package net.coplanar.ents;

import java.io.Serializable;
import java.lang.String;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;
import org.hibernate.annotations.NaturalId;

/**
 * Entity implementation class for Entity: Projects
 *
 */
@Entity

public class Project implements Serializable {

	   
	@Id
	@GeneratedValue ( strategy = GenerationType.IDENTITY )
	private int proj_id;
	@Column(name="job_name", nullable=false)
	private String pname;
	@NaturalId
	@Column(nullable=false)
	private String job_id;
	@OneToOne
	private TsUser rep_id;
	private static final long serialVersionUID = 1L;
	@OneToMany(
			mappedBy = "proj_id",
			cascade = CascadeType.ALL,
			orphanRemoval = true
	)
	private List<UserProject> projectUsers = new ArrayList<>(); 

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
	public String getJobId() {
		return this.job_id;
	}

	public void setJobId(String job_id) {
		this.job_id = job_id;
	}
	public TsUser getRep() {
		return this.rep_id;
	}

	public void setRep(TsUser rep) {
		this.rep_id = rep;
	}  
}
