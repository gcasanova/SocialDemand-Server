package app.domain.repositories.location.municipality;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import app.domain.entities.location.Municipality;

public class MunicipalityRepositoryImpl implements MunicipalityRepositoryCustom {
	
	@PersistenceContext
	private EntityManager em;

	@Override
	public Municipality findNearest(String name, Double longitude, Double latitude) {
		Query q = em.createNativeQuery("SELECT * FROM (SELECT `id`, `name`, `longitude`, `latitude`, `province_id`, 3956 * ACOS(COS(RADIANS(" + latitude  + ")) * COS(RADIANS(`latitude`)) * COS(RADIANS(" + longitude  + ") - RADIANS(`longitude`)) + SIN(RADIANS(" + latitude  + ")) * SIN(RADIANS(`latitude`))) AS `distance` FROM `municipalities` WHERE `latitude` BETWEEN " + latitude  + " - (10 / 69) AND " + latitude  + " + (10 / 69) AND `longitude` BETWEEN " + longitude  + " - (10 / (69 * COS(RADIANS(" + latitude  + ")))) AND " + longitude  + " + (10 / (69* COS(RADIANS(" + latitude  + "))))) r WHERE `distance` < 10  AND `name` COLLATE UTF8_GENERAL_CI LIKE '" + name + "' ORDER BY `distance` ASC LIMIT 1", Municipality.class);
		List<?> results = q.getResultList();
		if (!results.isEmpty()) {
			return (Municipality) results.get(0);
		}
		return null;
	}
}
