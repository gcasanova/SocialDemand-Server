package app.domain.repositories.location.municipality;

import app.domain.entities.location.Municipality;

public interface MunicipalityRepositoryCustom {
	Municipality findNearest(String name, Double longitude, Double latitude);
}
