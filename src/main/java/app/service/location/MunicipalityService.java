package app.service.location;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import app.domain.entities.location.Municipality;

@Validated
public interface MunicipalityService {

	Municipality getMunicipality(
			@NotNull(message = "{validate.municipalityService.getMunicipality.id}") Integer id);
	
	List<Municipality> getMunicipalityByProvinceId(
			@NotNull(message = "{validate.municipalityService.getMunicipalityByProvinceId.provinceId}") Integer provinceId);
	
	Municipality getNearest(
			@NotNull(message = "{validate.municipalityService.getNearest.name}") String name,
			@NotNull(message = "{validate.municipalityService.getNearest.longitude}") Double longitude,
			@NotNull(message = "{validate.municipalityService.getNearest.latitude}") Double latitude);

	Municipality save(
			@NotNull(message = "{validate.municipalityService.save.municipality}") @Valid Municipality municipality);

	void deleteMunicipality(Integer id);
}
