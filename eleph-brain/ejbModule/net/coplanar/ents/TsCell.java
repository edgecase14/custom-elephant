package net.coplanar.ents;

import java.io.Serializable;
import javax.persistence.*;

/**
 * Entity implementation class for Entity: TsCell
 *
 */
@Entity

public class TsCell implements Serializable {

	   
	@Id
	@GeneratedValue ( strategy = GenerationType.IDENTITY )
	private int id;
	
	@ManyToOne
	@JoinColumn( name="user_id",
			foreignKey = @ForeignKey(name="USER_ID_FK")
	)
	private TsUser tsuser;
	
	@ManyToOne
	@JoinColumn( name="proj_id",
			foreignKey = @ForeignKey(name="PROJ_ID_FK")
	)
	private Project project;
	
	private float entry;
	private static final long serialVersionUID = 1L;

	public TsCell() {
		super();
	}   
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}   
	public float getEntry() {
		return this.entry;
	}

	public void setEntry(float entry) {
		this.entry = entry;
	}
   
}
