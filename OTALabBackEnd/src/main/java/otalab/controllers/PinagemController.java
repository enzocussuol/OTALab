package otalab.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import otalab.models.Pinagem;
import otalab.models.Servico;
import otalab.repo.PinagemRepo;
import otalab.repo.ServicoRepo;

@RestController
public class PinagemController {
    @Autowired
    PinagemRepo pinRepo;

    @Autowired
    ServicoRepo servRepo;

    @Operation(summary = "Lê todas as pinagens existentes no momento.")
    @GetMapping("/pinagens/read")
    public List<Pinagem> readPinagens(){
        return pinRepo.findAll();
    }

    @Operation(summary = "Lê uma pinagem com um dado id.")
    @GetMapping("/pinagens/read/{idPinagem}")
    public ResponseEntity<Pinagem> readPinagemById(@PathVariable long idPinagem){
        Pinagem pin = pinRepo.findById(idPinagem).orElse(null);

        if(pin == null) return ResponseEntity.badRequest().body(null);
        return ResponseEntity.ok().body(pin);
    }

    @Operation(summary = "Cria uma pinagem.")
    @PostMapping("/pinagens/create")
    public ResponseEntity<String> createPinagem(String pinoDispositivo, String pinoSensor, long idServico){
        Servico serv = servRepo.findById(idServico).orElse(null);
        if(serv == null) return ResponseEntity.badRequest().body("Não foi possível encontrar um serviço com o id fornecido, logo, a pinagem não foi criada");

        Pinagem pin = new Pinagem(pinoDispositivo, pinoSensor, serv);
        pin = pinRepo.save(pin);

        if(pin == null) return ResponseEntity.badRequest().body("Não foi possível criar a pinagem");
        return ResponseEntity.ok().body("Pinagem criada com sucesso");
    }

    @Operation(summary = "Deleta uma pinagem com um dado id.")
    @DeleteMapping("/pinagens/delete/{idPinagem}")
    public ResponseEntity<String> deletePinagem(@PathVariable long idPinagem){
        Pinagem pin = pinRepo.findById(idPinagem).orElse(null);
        if(pin == null) return ResponseEntity.badRequest().body("Pinagem não existe");

        pinRepo.delete(pin);
        return ResponseEntity.ok().body("Pinagem deletada com sucesso");
    }
}
