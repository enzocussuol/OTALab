package otalab.controllers;

import java.util.List;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import otalab.models.Configuracao;
import otalab.repo.ConfiguracaoRepo;
import otalab.util.Processo;

@RestController
public class ConfiguracaoController {
    @Autowired
    ConfiguracaoRepo configRepo;

    @GetMapping("/configuracoes/read")
    public List<Configuracao> readConfiguracoes(){
        return configRepo.findAll();
    }

    @GetMapping("/configuracoes/read/{idConfiguracao}")
    public ResponseEntity<Configuracao> readConfiguracaoById(@PathVariable long idConfiguracao){
        Configuracao config = configRepo.findById(idConfiguracao).orElse(null);

        if(config == null) return ResponseEntity.badRequest().body(null);
        return ResponseEntity.ok(config);
    }

    @PostMapping("/configuracoes/create")
    public ResponseEntity<String> createConfiguracao(String nomeWiFi, String senhaWiFi, String ipBroker){
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword("wifi");

        Configuracao config = new Configuracao(nomeWiFi, encryptor.encrypt(senhaWiFi), ipBroker);
        config = configRepo.save(config);

        if(config == null) return ResponseEntity.badRequest().body("Não foi possível criar a configuração");
        return ResponseEntity.ok("Configuração criada com sucesso");
    }

    @PutMapping("/configuracoes/setAsActive/{idConfiguracao}")
    public ResponseEntity<String> ativaConfiguracao(@PathVariable long idConfiguracao){
        Configuracao config = configRepo.findById(idConfiguracao).orElse(null);

        if(config == null) return ResponseEntity.badRequest().body("Configuração não encontrada");

        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword("wifi");

        Processo processo = new Processo();

        processo.executa("bash scripts/injetaConfigLib.sh " + config.getNomeWiFi() + " " +
                        encryptor.decrypt(config.getSenhaWiFi()) + " " + config.getIpBroker());
        if(!processo.getExitCode()) return new ResponseEntity<>("Erro ao injetar a configuração na biblioteca", HttpStatus.INTERNAL_SERVER_ERROR);

        config.setAtiva(true);
        configRepo.save(config);

        List<Configuracao> configs = configRepo.findAll();

        for(Configuracao cfg: configs){
            if(cfg.getAtiva() && cfg.getIdConfiguracao() != idConfiguracao){
                cfg.setAtiva(false);
                configRepo.save(cfg);
            }
        }

        return ResponseEntity.ok("Configuração atualizada como ativa com sucesso");
    }

    @DeleteMapping("/configuracoes/delete/{idConfiguracao}")
    public ResponseEntity<String> deleteConfiguracao(@PathVariable long idConfiguracao){
        Configuracao config = configRepo.findById(idConfiguracao).orElse(null);
        if(config == null) return ResponseEntity.badRequest().body("Configuração não existe");

        configRepo.delete(config);
        return ResponseEntity.ok("Configuração deletada com sucesso");
    }
}
