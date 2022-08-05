package otalab.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import otalab.models.Conexao;

public interface ConexaoRepo extends JpaRepository<Conexao, Long>{
    
}
