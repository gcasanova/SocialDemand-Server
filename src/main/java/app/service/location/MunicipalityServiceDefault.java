package app.service.location;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import app.domain.entities.location.Municipality;
import app.domain.repositories.location.municipality.MunicipalityRepository;

@Service
public class MunicipalityServiceDefault implements MunicipalityService {

	@Autowired
	MunicipalityRepository municipalityRepository;

	@Override
	public Municipality getMunicipality(Integer id) {
		return this.municipalityRepository.findOne(id);
	}
	
	@Override
	public List<Municipality> getMunicipalityByProvinceId(Integer provinceId) {
		return this.municipalityRepository.findByProvinceId(provinceId);
	}	

	@Override
	public Municipality getNearest(String name, Double longitude, Double latitude) {
		return this.municipalityRepository.findNearest(name, longitude, latitude);
	}
	
	@Override
	public Municipality save(Municipality user) {
		return this.municipalityRepository.save(user);
	}

	@Override
	public void deleteMunicipality(Integer id) {
		this.municipalityRepository.delete(id);
	}
}
