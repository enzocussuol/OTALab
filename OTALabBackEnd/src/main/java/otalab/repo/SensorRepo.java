package otalab.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import otalab.models.Sensor;

public interface SensorRepo extends JpaRepository<Sensor, Long>{
    
}
