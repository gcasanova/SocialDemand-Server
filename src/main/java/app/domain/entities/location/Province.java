package app.domain.entities.location;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "provinces")
public class Province implements Serializable {
	private static final long serialVersionUID = 646350072077527619L;
	
	private Integer id;
	private String name;
	private Integer RegionId;
	
	@Id
	@JsonProperty
	@GeneratedValue(strategy = GenerationType.AUTO)
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
	@Column(name="region_id")
	public Integer getRegionId() {
		return RegionId;
	}
	public void setRegionId(Integer regionId) {
		RegionId = regionId;
	}
}
