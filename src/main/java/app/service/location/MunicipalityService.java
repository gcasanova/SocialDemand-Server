package app.service.location;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import app.domain.entities.location.Municipality;

@Validated
public interface MunicipalityService {

	Municipality getMunicipality(
			@NotNull( message = "{validate.municipalityService.getMunicipality.id}") Integer id);

	Municipality save(
			@NotNull(message = "{validate.municipalityService.save.municipality}") @Valid Municipality municipality);

	void deleteMunicipality(Integer id);
}
