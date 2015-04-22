package app.service.location;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import app.domain.entities.location.Region;

@Validated
public interface RegionService {

	Region getRegion(
			@NotNull( message = "{validate.regionService.getRegion.id}") Integer id);

	Region save(
			@NotNull(message = "{validate.regionService.save.region}") @Valid Region region);

	void deleteRegion(Integer id);
}
