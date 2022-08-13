package otalab.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import otalab.models.Configuracao;
import otalab.models.Dispositivo;
import otalab.repo.ConfiguracaoRepo;
import otalab.repo.DispositivoRepo;
import otalab.util.Processo;

@RestController
public class DispositivoController {
    @Autowired
    DispositivoRepo dispRepo;

    @Autowired
    ConfiguracaoRepo configRepo;

    @Operation(summary = "Lê todos os dispositivos existentes no momento.")
    @GetMapping("/dispositivos/read")
    public List<Dispositivo> readDispositivos(){
        return dispRepo.findAll();
    }

    @Operation(summary = "Lê um dispositivo com um dado id.")
    @GetMapping("/dispositivos/read/{idDispositivo}")
    public ResponseEntity<Dispositivo> readDispositivoById(@PathVariable long idDispositivo){
        Dispositivo disp = dispRepo.findById(idDispositivo).orElse(null);

        if(disp == null) return ResponseEntity.badRequest().body(null);
        return ResponseEntity.ok().body(disp);
    }

    @Operation(summary = "Cria um dispositivo. O dispositivo deve estar conectado à maquina hospedeira do OTALab via USB para ser cadastrado.")
    @PostMapping("/dispositivos/create")
    public ResponseEntity<String> createDispositivo(String nome, String descricao, String placa, String portaCadastro){
        Configuracao config = configRepo.findByAtiva(true);
        if(config == null) return ResponseEntity.badRequest().body("Impossível criar um dispositivo sem que haja uma configuração ativa");

        Dispositivo disp = new Dispositivo(nome, descricao, placa, portaCadastro);
        disp = dispRepo.save(disp);
        if(disp == null) return ResponseEntity.badRequest().body("Não foi possível criar o dispositivo");

        Processo processo = new Processo();

        processo.executa("bash scripts/copiaTemplate.sh " + disp.getIdDispositivo());
		if(!processo.getExitCode()){
            dispRepo.delete(disp);
            return new ResponseEntity<>("Erro na cópia do template", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        processo.executa("arduino-cli compile -b " + placa + " OTADefault");
		if(!processo.getExitCode()){
            dispRepo.delete(disp);
            return new ResponseEntity<>("Erro ao compilar o código de cadastro com o arduino-cli:\n\n" +
                                        processo.getStdErr(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        processo.executa("arduino-cli upload -b " + placa + " -p /dev/" + portaCadastro + " OTADefault");
		if(!processo.getExitCode()){
            dispRepo.delete(disp);
            return new ResponseEntity<>("Erro ao fazer upload do código de cadastro com o arduino-cli:\n\n" +
                                        processo.getStdErr(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        return ResponseEntity.ok("Dispositivo criado com sucesso");
    }

    @Operation(summary = "Deleta um dispositivo com um dado id.")
    @DeleteMapping("/dispositivos/delete/{idDispositivo}")
    public ResponseEntity<String> deleteDispositivo(@PathVariable long idDispositivo){
        Dispositivo disp = dispRepo.findById(idDispositivo).orElse(null);
        if(disp == null) return ResponseEntity.badRequest().body("Dispositivo não existe");

        dispRepo.delete(disp);
        return ResponseEntity.ok().body("Dispositivo deletado com sucesso");
    }
}