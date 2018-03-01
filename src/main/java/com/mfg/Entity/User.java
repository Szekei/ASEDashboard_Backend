package com.mfg.Entity;

import java.sql.Timestamp;
import java.util.Date;
import javax.persistence.*;

/**
 * 
 * @author I337864
 *
 */
@Entity
public class User {
	@Id
	@GeneratedValue
	private Long id;
	
	@Column(nullable = false)
	private String sapId;

	@Column(nullable = false)
	private String userName;
	
	@Column(nullable = false)
	private String password;
	
	@Column(nullable = false)
	private String email;
	
	@Column(nullable = false)
	private Timestamp createdTime;
	
	public User() {}

	public User(String sapId, String userName, String password, String email, Timestamp createdTime) {
		super();
		this.sapId = sapId;
		this.userName = userName;
		this.password = password;
		this.email = email;
		this.createdTime = createdTime;
	}

	public String getSapId() {
		return sapId;
	}

	public void setSapId(String sapId) {
		this.sapId = sapId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Timestamp getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Timestamp createdTime) {
		this.createdTime = createdTime;
	}

	@PrePersist
	protected void onPersist(){
		this.createdTime = now();
	}

	protected static Timestamp now(){
		return new Timestamp(new Date().getTime());
	}
	
	
}