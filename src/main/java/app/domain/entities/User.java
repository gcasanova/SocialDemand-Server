package app.domain.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import app.domain.entities.location.Location;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "users")
public class User implements Serializable {
	private static final long serialVersionUID = -947757741988164699L;
	
	private Integer id;
	private String name;
	private String email;
	private String phone;
	private String document; // official identification document
	private String password;
	private Integer municipalityId;
	
	private Location location; // transient property populated when appropriate
	
	@Id
	@JsonProperty
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@JsonProperty
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@JsonProperty
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	@JsonIgnore
	public String getPhone() {
		return phone;
	}
	@JsonProperty
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	@JsonIgnore
	public String getDocument() {
		return document;
	}
	@JsonProperty
	public void setDocument(String document) {
		this.document = document;
	}
	
	@JsonIgnore
	public String getPassword() {
		return password;
	}
	@JsonProperty
	public void setPassword(String password) {
		this.password = password;
	}
	
	@JsonProperty
	@Column(name="municipality_id")
	public Integer getMunicipalityId() {
		return municipalityId;
	}
	public void setMunicipalityId(Integer municipalityId) {
		this.municipalityId = municipalityId;
	}
	
	@Transient
	@JsonProperty
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
}
