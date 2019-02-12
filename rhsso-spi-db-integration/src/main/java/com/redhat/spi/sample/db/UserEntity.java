package com.redhat.spi.sample.db;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@NamedQueries({ @NamedQuery(name = "getUserByUsername", query = "select u from UserEntity u where u.name = :name"),
		@NamedQuery(name = "getUserCount", query = "select count(u) from UserEntity u"),
		@NamedQuery(name = "getAllUsers", query = "select u from UserEntity u"),
		// @NamedQuery(name="searchForUser", query="select u from UserEntity u where " +
		// "( lower(u.username) like :search or u.email like :search ) order by
		// u.username"),
})
@Entity(name = "UserEntity")
@Table(name = "user")
public class UserEntity {
	//
	@Id
	private String id;
	private String name;
	private String pwd;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return name;
	}

	public void setUsername(String username) {
		this.name = username;
	}

	public String getPassword() {
		return pwd;
	}

	public void setPassword(String password) {
		this.pwd = password;
	}
}
