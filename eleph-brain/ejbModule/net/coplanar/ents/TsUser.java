package net.coplanar.ents;

import java.io.Serializable;
import java.lang.String;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.NaturalId;
//import org.hibernate.annotations.*;


/**
 * Entity implementation class for Entity: Users
 *
 */
@Entity

public class TsUser implements Serializable {

	   
	@Id
	@GeneratedValue ( strategy = GenerationType.IDENTITY )
	private int user_id;
    @NaturalId
    @Column(name="username", nullable=false, unique=true)
	private String username;
    private static final long serialVersionUID = 1L;
	@ColumnDefault("0.04")
	private float vac_rate;
	
	@OneToMany(
			mappedBy = "user_id",
			cascade = CascadeType.ALL,
			orphanRemoval = true,
			fetch = FetchType.EAGER
	)
	private List<UserProject> userProjects = new ArrayList<>(); 

	public TsUser() {
		super();
	}   
	public int getUser_id() {
		return this.user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}   
	public String getUsername() {
		return this.username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

	public float getVacRate() {
		return this.vac_rate;
	}
	public void setVacRate(float vac_rate) {
		this.vac_rate = vac_rate;
	}

	public List<UserProject> getProjects() {
		return this.userProjects;
	}
   
}
