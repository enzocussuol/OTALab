package otalab.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import otalab.models.Pinagem;
import otalab.repo.PinagemRepo;

@RestController
public class PinagemController {
    @Autowired
    PinagemRepo pinRepo;

    @GetMapping("/pinagens/read")
    public List<Pinagem> readPinagens(){
        return pinRepo.findAll();
    }

    @GetMapping("/pinagens/read/{idPinagem}")
    public ResponseEntity<Pinagem> readPinagemById(@PathVariable long idPinagem){
        Pinagem pin = pinRepo.findById(idPinagem).orElse(null);

        if(pin == null) return ResponseEntity.badRequest().body(null);
        return ResponseEntity.ok().body(pin);
    }

    @PostMapping("/pinagens/create")
    public ResponseEntity<String> createPinagem(String pinoDispositivo, String pinoSensor){
        Pinagem pin = new Pinagem(pinoDispositivo, pinoSensor);
        pin = pinRepo.save(pin);

        if(pin == null) return ResponseEntity.badRequest().body("Não foi possível criar a pinagem");
        return ResponseEntity.ok().body("Pinagem criada com sucesso");
    }
}
