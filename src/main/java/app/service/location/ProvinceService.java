package app.service.location;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import app.domain.entities.location.Province;

@Validated
public interface ProvinceService {

	Province getProvince(
			@NotNull( message = "{validate.provinceService.getProvince.id}") Integer id);

	Province save(
			@NotNull(message = "{validate.provinceService.save.province}") @Valid Province province);

	void deleteProvince(Integer id);
}
