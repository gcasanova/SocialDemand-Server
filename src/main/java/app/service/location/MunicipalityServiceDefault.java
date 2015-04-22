package app.service.location;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import app.domain.entities.location.Municipality;
import app.domain.repositories.location.MunicipalityRepository;

@Service
public class MunicipalityServiceDefault implements MunicipalityService {

	@Autowired
	MunicipalityRepository municipalityRepository;

	@Override
	public Municipality getMunicipality(Integer id) {
		return this.municipalityRepository.findOne(id);
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
