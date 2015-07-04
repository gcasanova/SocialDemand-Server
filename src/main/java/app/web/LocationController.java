package app.web;



import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import app.domain.entities.location.Municipality;
import app.domain.entities.location.Province;
import app.domain.entities.location.Region;
import app.service.location.MunicipalityService;
import app.service.location.ProvinceService;
import app.service.location.RegionService;

@RestController
@RequestMapping("/api")
@SuppressWarnings("unchecked")
public class LocationController {

	@Autowired
	private MunicipalityService municipalityService;
	@Autowired
	private ProvinceService provinceService;
	@Autowired
	private RegionService regionService;

	@RequestMapping(value = "/municipalities/{id}", method = RequestMethod.GET)
	public ResponseEntity<JSONObject> readMunicipality(@PathVariable("id") Integer id) {
		Municipality aMunicipality = this.municipalityService.getMunicipality(id);
		if (aMunicipality == null)
			return new ResponseEntity<JSONObject>(HttpStatus.NOT_FOUND);
		
		JSONObject json = new JSONObject();
		json.put("municipality", aMunicipality);
		return new ResponseEntity<JSONObject>(json, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/municipalities", method = RequestMethod.GET)
	public ResponseEntity<JSONObject> readNearest(
			@RequestParam(value = "provinceId", required = false) Integer provinceId,
			@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "longitude", required = false) Double longitude,
			@RequestParam(value = "latitude", required = false) Double latitude) {
		
		if (name != null && longitude != null && latitude != null) {
			Municipality aMunicipality = this.municipalityService.getNearest(name, longitude, latitude);
			if (aMunicipality == null)
				return new ResponseEntity<JSONObject>(HttpStatus.NOT_FOUND);

			JSONArray jsonArray = new JSONArray();
			jsonArray.add(aMunicipality);
			JSONObject json = new JSONObject();
			json.put("municipalities", jsonArray);
			return new ResponseEntity<JSONObject>(json, HttpStatus.OK);
		} else if (provinceId != null) {
			JSONArray jsonArray = new JSONArray();
			for (Municipality aMunicipality : this.municipalityService.getMunicipalityByProvinceId(provinceId)) {
				jsonArray.add(aMunicipality);
			}
			JSONObject json = new JSONObject();
			json.put("municipalities", jsonArray);
			return new ResponseEntity<JSONObject>(json, HttpStatus.OK);
		}
		return new ResponseEntity<JSONObject>(HttpStatus.BAD_REQUEST);
	}
	
	@RequestMapping(value = "/provinces/{id}", method = RequestMethod.GET)
	public ResponseEntity<JSONObject> readProvince(@PathVariable("id") Integer id) {
		Province mProvince = this.provinceService.getProvince(id);
		if (mProvince == null)
			return new ResponseEntity<JSONObject>(HttpStatus.NOT_FOUND);
		
		JSONObject json = new JSONObject();
		json.put("province", mProvince);
		return new ResponseEntity<JSONObject>(json, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/provinces", method = RequestMethod.GET)
	public ResponseEntity<JSONObject> readByRegionId(@RequestParam("regionId") Integer regionId) {
		JSONArray jsonArray = new JSONArray();
		for (Province aProvince : this.provinceService.getProvinceByRegionId(regionId)) {
			jsonArray.add(aProvince);
		}
		JSONObject json = new JSONObject();
		json.put("provinces", jsonArray);
		return new ResponseEntity<JSONObject>(json, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/regions/{id}", method = RequestMethod.GET)
	public ResponseEntity<JSONObject> readRegion(@PathVariable("id") Integer id) {
		Region aRegion = this.regionService.getRegion(id);
		if (aRegion == null)
			return new ResponseEntity<JSONObject>(HttpStatus.NOT_FOUND);
		
		JSONObject json = new JSONObject();
		json.put("region", aRegion);
		return new ResponseEntity<JSONObject>(json, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/regions", method = RequestMethod.GET)
	public ResponseEntity<JSONObject> readAllRegions() {
		JSONObject json = new JSONObject();
		json.put("regions", this.regionService.getAllRegions());
		return new ResponseEntity<JSONObject>(json, HttpStatus.OK);
	}
}
