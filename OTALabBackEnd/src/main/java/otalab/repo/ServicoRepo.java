package otalab.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import otalab.models.Servico;

public interface ServicoRepo extends JpaRepository<Servico, Long>{
    
}
