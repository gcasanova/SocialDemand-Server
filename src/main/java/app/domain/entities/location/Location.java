package app.domain.entities.location;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import app.service.location.MunicipalityService;
import app.service.location.ProvinceService;
import app.service.location.RegionService;

import com.fasterxml.jackson.annotation.JsonProperty;

@Component
public class Location implements Serializable {
	private static final long serialVersionUID = 9107553270251669786L;
	
	@Autowired
	private RegionService regionService;
	@Autowired
	private ProvinceService provinceService;
	@Autowired
	private MunicipalityService municipalityService;
	
	private Region region;
	private Province province;
	private Municipality municipality;
	
	public Location(){};
	
	public Location(Integer municipalityId) {
		municipality = this.municipalityService.getMunicipality(municipalityId);
		province = this.provinceService.getProvince(municipality.getProvinceId());
		region = this.regionService.getRegion(province.getRegionId());
	}
	
	@JsonProperty
	public Region getRegion() {
		return region;
	}
	public void setRegion(Region region) {
		this.region = region;
	}
	
	@JsonProperty
	public Province getProvince() {
		return province;
	}
	public void setProvince(Province province) {
		this.province = province;
	}
	
	@JsonProperty
	public Municipality getMunicipality() {
		return municipality;
	}
	public void setMunicipality(Municipality municipality) {
		this.municipality = municipality;
	}
	
	
}
