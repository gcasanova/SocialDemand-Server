package app.service.location;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import app.domain.entities.location.Province;

@Validated
public interface ProvinceService {

	Province getProvince(
			@NotNull( message = "{validate.provinceService.getProvince.id}") Integer id);
	
	List<Province> getProvinceByRegionId(
			@NotNull( message = "{validate.provinceService.getProvinceByRegionId.regionId}") Integer regionId);

	Province save(
			@NotNull(message = "{validate.provinceService.save.province}") @Valid Province province);

	void deleteProvince(Integer id);
}
