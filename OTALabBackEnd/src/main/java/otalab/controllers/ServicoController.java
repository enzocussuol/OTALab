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
import otalab.models.Dispositivo;
import otalab.models.Sensor;
import otalab.models.Servico;
import otalab.repo.DispositivoRepo;
import otalab.repo.PinagemRepo;
import otalab.repo.SensorRepo;
import otalab.repo.ServicoRepo;

@RestController
public class ServicoController {
    @Autowired
    ServicoRepo servRepo;

    @Autowired
    SensorRepo sensorRepo;

    @Autowired
    PinagemRepo pinRepo;

    @Autowired
    DispositivoRepo dispRepo;

    @Operation(summary = "Lê todos os serviços existentes no momento.")
    @GetMapping("/servicos/read")
    public List<Servico> readServicos(){
        return servRepo.findAll();
    }

    @Operation(summary = "Lê um serviço com um dado id.")
    @GetMapping("/servicos/read/{idServico}")
    public ResponseEntity<Servico> readServicoById(@PathVariable long idServico){
        Servico servico = servRepo.findById(idServico).orElse(null);

        if(servico == null) ResponseEntity.badRequest().body(null);
        return ResponseEntity.ok().body(servico);
    }

    @Operation(summary = "Cria um serviço.")
    @PostMapping("/servicos/create")
    public ResponseEntity<String> createServico(long idSensor, long idDispositivo){
        Sensor sensor = sensorRepo.findById(idSensor).orElse(null);
        if(sensor == null) return ResponseEntity.badRequest().body("Não foi possível encontrar um sensor com o id fornecido, logo, o serviço não foi criado");

        Dispositivo disp = dispRepo.findById(idDispositivo).orElse(null);
        if(disp == null) return ResponseEntity.badRequest().body("Não foi possível encontrar um dispositivo com o id fornecido, logo, o serviço não foi criado");

        Servico servico = new Servico(sensor, disp);
        servico = servRepo.save(servico);

        if(servico == null) ResponseEntity.badRequest().body("Não foi possível criar o serviço");
        
        return ResponseEntity.ok().body("Serviço criado com sucesso");
    }

    @Operation(summary = "Deleta um serviço com um dado id.")
    @DeleteMapping("/servicos/delete/{idServico}")
    public ResponseEntity<String> deleteServico(@PathVariable long idServico){
        Servico serv = servRepo.findById(idServico).orElse(null);
        if(serv == null) return ResponseEntity.badRequest().body("Serviço não existe");

        servRepo.delete(serv);
        return ResponseEntity.ok().body("Serviço deletado com sucesso");
    }
}
