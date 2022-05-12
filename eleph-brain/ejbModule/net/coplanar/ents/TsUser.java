package net.coplanar.ents;

import java.io.Serializable;
import java.lang.String;
import javax.persistence.*;

/**
 * Entity implementation class for Entity: Users
 *
 */
@Entity

public class TsUser implements Serializable {

	   
	@Id
	@GeneratedValue ( strategy = GenerationType.IDENTITY )
	private int user_id;
	private String username;
	private static final long serialVersionUID = 1L;

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
   
}
