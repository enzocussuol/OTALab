package otalab.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import otalab.models.Configuracao;


public interface ConfiguracaoRepo extends JpaRepository<Configuracao, Long>{
    
}
