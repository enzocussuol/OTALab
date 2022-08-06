package otalab.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fazecast.jSerialComm.SerialPort;

import otalab.util.Processo;

@RestController
public class RecursosController {
    @PostMapping("/recursos/setup")
    public ResponseEntity<String> setupRecursos(){
        Processo processo = new Processo();
        processo.executa("which arduino-cli");

        if(processo.getExitCode()) return new ResponseEntity<>("Recursos já estão configurados", HttpStatus.INTERNAL_SERVER_ERROR);

        processo.executa("scripts/install.sh");
        if(!processo.getExitCode()) return new ResponseEntity<>("Ocorreu um erro durante a execução do script de inicialização", HttpStatus.INTERNAL_SERVER_ERROR);

        return ResponseEntity.ok().body("Setup dos recursos completo com sucesso");
    }

    @GetMapping("/recursos/bibliotecas/read")
    public ResponseEntity<String> readBibliotecas(){
        Processo processo = new Processo();
        processo.executa("arduino-cli lib list");

        if(!processo.getExitCode()) return new ResponseEntity<>("Não foi possível obter as bibliotecas instaladas", HttpStatus.INTERNAL_SERVER_ERROR);

        return ResponseEntity.ok().body(processo.getStdOut());
    }

    @PostMapping("/recursos/bibliotecas/install/{nomeBiblioteca}")
    public ResponseEntity<String> installBiblioteca(@PathVariable String nomeBiblioteca){
        Processo processo = new Processo();

        processo.executa("arduino-cli lib install " + nomeBiblioteca);

        if(!processo.getExitCode()) return new ResponseEntity<>("Não foi possível instalar essa biblioteca:\n\n" +
                                                                processo.getStdErr(), HttpStatus.INTERNAL_SERVER_ERROR);

        return ResponseEntity.ok().body("Biblioteca instalada com sucesso");
    }

    @DeleteMapping("/recursos/bibliotecas/uninstall/{nomeBiblioteca}")
    public ResponseEntity<String> uninstallBiblioteca(@PathVariable String nomeBiblioteca){
        Processo processo = new Processo();

        processo.executa("arduino-cli lib uninstall " + nomeBiblioteca);

        if(!processo.getExitCode()) return new ResponseEntity<>("Não foi possível desinstalar essa biblioteca:\n\n" +
                                                                processo.getStdErr(), HttpStatus.INTERNAL_SERVER_ERROR);

        return ResponseEntity.ok().body("Biblioteca desinstalada com sucesso");
    }

    @GetMapping("/recursos/placas/read")
    public ResponseEntity<String> readPlacas(){
        Processo processo = new Processo();
        processo.executa("arduino-cli board listall");
        
        if(!processo.getExitCode()) return new ResponseEntity<>("Nao foi possível obter as placas instaladas", HttpStatus.INTERNAL_SERVER_ERROR);

        return ResponseEntity.ok().body(processo.getStdOut());
    }

    @GetMapping("/recursos/portas/read")
    public ResponseEntity<String> readPortas(){
        SerialPort[] portas = SerialPort.getCommPorts();
        String portasAsStr = "";

        for(SerialPort porta: portas){
            portasAsStr += porta.getSystemPortName() + "\n";
        }

        return ResponseEntity.ok().body(portasAsStr);
    }
}
