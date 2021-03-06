package app.service.location;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import app.domain.entities.location.Province;
import app.domain.repositories.location.ProvinceRepository;

@Service
public class ProvinceServiceDefault implements ProvinceService {

	@Autowired
	ProvinceRepository provinceRepository;

	@Override
	public Province getProvince(Integer id) {
		return this.provinceRepository.findOne(id);
	}
	
	@Override
	public List<Province> getProvinceByRegionId(Integer regionId) {
		return this.provinceRepository.findByRegionId(regionId);
	}

	@Override
	public Province save(Province user) {
		return this.provinceRepository.save(user);
	}

	@Override
	public void deleteProvince(Integer id) {
		this.provinceRepository.delete(id);
	}
}
