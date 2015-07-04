package app.domain.repositories.location.municipality;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import app.domain.entities.location.Municipality;

@Repository
public interface MunicipalityRepository extends CrudRepository<Municipality, Integer>, MunicipalityRepositoryCustom {
	List<Municipality> findByProvinceId(Integer provinceId);
}
