package otalab.controllers;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fazecast.jSerialComm.SerialPort;

import io.swagger.v3.oas.annotations.Operation;
import otalab.util.Processo;

@RestController
public class RecursosController {
    @Operation(summary = "Lê todas as bibliotecas instaladas no momento.")
    @GetMapping("/recursos/bibliotecas/read")
    public ResponseEntity<String> readBibliotecas(){
        Processo processo = new Processo();
        processo.executa("arduino-cli lib list");

        if(!processo.getExitCode()) return new ResponseEntity<>("Não foi possível obter as bibliotecas instaladas", HttpStatus.INTERNAL_SERVER_ERROR);

        return ResponseEntity.ok().body(processo.getStdOut());
    }

    @Operation(summary = "Instala uma biblioteca com um dado nome. A biblioteca deve estar no repositório oficial Arduino.")
    @PostMapping("/recursos/bibliotecas/install/{nomeBiblioteca}")
    public ResponseEntity<String> installBiblioteca(@PathVariable String nomeBiblioteca){
        Processo processo = new Processo();

        processo.executa("arduino-cli lib install " + nomeBiblioteca);

        if(!processo.getExitCode()) return new ResponseEntity<>("Não foi possível instalar essa biblioteca:\n\n" +
                                                                processo.getStdErr(), HttpStatus.INTERNAL_SERVER_ERROR);

        return ResponseEntity.ok().body("Biblioteca instalada com sucesso");
    }

    @Operation(summary = "Desinstala uma biblioteca com um dado nome.")
    @DeleteMapping("/recursos/bibliotecas/uninstall/{nomeBiblioteca}")
    public ResponseEntity<String> uninstallBiblioteca(@PathVariable String nomeBiblioteca){
        Processo processo = new Processo();

        processo.executa("arduino-cli lib uninstall " + nomeBiblioteca);

        if(!processo.getExitCode()) return new ResponseEntity<>("Não foi possível desinstalar essa biblioteca:\n\n" +
                                                                processo.getStdErr(), HttpStatus.INTERNAL_SERVER_ERROR);

        return ResponseEntity.ok().body("Biblioteca desinstalada com sucesso");
    }

    @Operation(summary = "Lê todas as placas instaladas no momento.")
    @GetMapping("/recursos/placas/read")
    public ResponseEntity<String> readPlacas(){
        Processo processo = new Processo();
        processo.executa("arduino-cli board listall");
        
        if(!processo.getExitCode()) return new ResponseEntity<>("Nao foi possível obter as placas instaladas", HttpStatus.INTERNAL_SERVER_ERROR);

        return ResponseEntity.ok().body(processo.getStdOut());
    }

    @Operation(summary = "Lê todas as portas USB conectadas no momento.")
    @GetMapping("/recursos/portas/read")
    public List<String> readPortas(){
        SerialPort[] portas = SerialPort.getCommPorts();
        List<String> portasAsStr = new ArrayList<>();

        for(SerialPort porta: portas){
            portasAsStr.add(porta.getSystemPortName());
        }

        return portasAsStr;
    }
}
