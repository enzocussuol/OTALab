package otalab.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import otalab.models.Sensor;
import otalab.repo.SensorRepo;

@RestController
public class SensorController {
    @Autowired
    SensorRepo sensorRepo;

    @GetMapping("/sensores/read")
    public List<Sensor> readSensores(){
        return sensorRepo.findAll();
    }

    @GetMapping("/sensores/read/{idSensor}")
    public ResponseEntity<Sensor> readSensorById(@PathVariable long idSensor){
        Sensor sensor = sensorRepo.findById(idSensor).orElse(null);

        if(sensor == null) ResponseEntity.badRequest().body(null);
        return ResponseEntity.ok().body(sensor);
    }

    @PostMapping("/sensores/create")
    public ResponseEntity<String> createSensor(String nome, String descricao){
        Sensor sensor = new Sensor(nome, descricao);
        sensor = sensorRepo.save(sensor);

        if(sensor == null) return ResponseEntity.badRequest().body("Não foi possível criar o sensor");
        return ResponseEntity.ok().body("Sensor criado com sucesso");
    }
}
