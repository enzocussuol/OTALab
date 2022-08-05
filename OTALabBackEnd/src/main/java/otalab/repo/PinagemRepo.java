package otalab.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import otalab.models.Pinagem;

public interface PinagemRepo extends JpaRepository<Pinagem, Long>{
    
}
