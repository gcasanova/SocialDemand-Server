package app.domain.repositories.location;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import app.domain.entities.location.Province;

@Repository
public interface ProvinceRepository extends CrudRepository<Province, Integer> {
}
