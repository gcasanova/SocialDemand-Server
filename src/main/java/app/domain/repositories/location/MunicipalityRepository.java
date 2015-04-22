package app.domain.repositories.location;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import app.domain.entities.location.Municipality;

@Repository
public interface MunicipalityRepository extends CrudRepository<Municipality, Integer> {
}
