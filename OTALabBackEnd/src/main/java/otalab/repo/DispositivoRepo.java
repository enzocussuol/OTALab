package otalab.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import otalab.models.Dispositivo;


public interface DispositivoRepo extends JpaRepository<Dispositivo, Long>{
    
}
