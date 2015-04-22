package app.service.location;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import app.domain.entities.location.Region;
import app.domain.repositories.location.RegionRepository;

@Service
public class RegionServiceDefault implements RegionService {

	@Autowired
	RegionRepository regionRepository;

	@Override
	public Region getRegion(Integer id) {
		return this.regionRepository.findOne(id);
	}

	@Override
	public Region save(Region user) {
		return this.regionRepository.save(user);
	}

	@Override
	public void deleteRegion(Integer id) {
		this.regionRepository.delete(id);
	}
}
