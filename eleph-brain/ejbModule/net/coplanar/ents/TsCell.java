package net.coplanar.ents;

import java.io.Serializable;
import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Entity implementation class for Entity: TsCell
 *
 */
@Entity
@Table (
		indexes = @Index (name = "idx_te_date", columnList = "te_date", unique = false)
		)

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
	
	@Column(nullable=false)
	private LocalDate te_date;
	@Column(nullable=false)
	private float entry;
	private String te_note;
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
	public Project getProject() {
		return this.project;
	}
	public TsUser getTsUser() {
		return this.tsuser;
	}
	public LocalDate getDate() {
		return this.te_date;
	}
	public void setDate(LocalDate dt) {
		this.te_date = dt;
	}
	public String getNote() {
		return this.te_note;
	}
	public void setNote(String note) {
		this.te_note = note;
	}


   
}
