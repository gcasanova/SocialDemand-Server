package app.domain.repositories.location;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import app.domain.entities.location.Region;

@Repository
public interface RegionRepository extends CrudRepository<Region, Integer> {
}
