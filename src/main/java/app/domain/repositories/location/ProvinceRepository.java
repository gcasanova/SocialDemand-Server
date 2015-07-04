package app.domain.repositories.location;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import app.domain.entities.location.Province;

@Repository
public interface ProvinceRepository extends CrudRepository<Province, Integer> {
	List<Province> findByRegionId(Integer regionId);
}
