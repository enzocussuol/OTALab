package otalab.controllers;

import java.io.File;
import java.util.List;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import otalab.models.Configuracao;
import otalab.models.Dispositivo;
import otalab.repo.ConfiguracaoRepo;
import otalab.repo.DispositivoRepo;
import otalab.util.ProcessoBash;



@RestController
public class DispositivoController {
    @Autowired
    DispositivoRepo dispRepo;

    @Autowired
    ConfiguracaoRepo configRepo;

    @GetMapping("/dispositivos/read")
    public List<Dispositivo> readDispositivos(){
        return dispRepo.findAll();
    }

    @GetMapping("/dispositivos/read/{idDispositivo}")
    public ResponseEntity<Dispositivo> readDispositivoById(@PathVariable long idDispositivo){
        Dispositivo disp = dispRepo.getById(idDispositivo);

        if(disp == null) return ResponseEntity.badRequest().body(null);
        return ResponseEntity.ok().body(disp);
    }

    @PostMapping("/dispositivos/create")
    public ResponseEntity<String> createDispositivo(String nome, String descricao, String placa, String portaCadastro, long idConfiguracao){		
		String pathOTADefault = System.getProperty("user.home") + "/OTALab/OTADefault";
		File diretorioOTADefault = new File(pathOTADefault);
		if(!diretorioOTADefault.exists()) diretorioOTADefault.mkdir();
		
        Configuracao config = configRepo.getById(idConfiguracao);
		if(config == null) return ResponseEntity.badRequest().body("Erro ao buscar a configuração com o id fornecido");

		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword("wifi");

        Dispositivo disp = new Dispositivo(nome, descricao, placa, portaCadastro);

		if(!ProcessoBash.runProcess("cp OTATemplate.ino OTADefault/OTADefault.ino"))
			return new ResponseEntity<>("Erro na cópia do template", HttpStatus.INTERNAL_SERVER_ERROR);
		
		if(!ProcessoBash.runProcess("bash Scripts/injetaConfigLib.sh " + config.getNomeWiFi() + " " +
                                    encryptor.decrypt(config.getSenhaWiFi()) + " " + config.getIpBroker() + " " + disp.getId()))
            return new ResponseEntity<>("Erro ao injetar as informações do dispositivo no código fonte", HttpStatus.INTERNAL_SERVER_ERROR);

		if(!ProcessoBash.runProcess("arduino-cli compile -b " + placa + " OTADefault"))
			return new ResponseEntity<>("Erro ao compilar o código de cadastro com o arduino-cli", HttpStatus.INTERNAL_SERVER_ERROR);

        if(!ProcessoBash.runProcess("bash Scripts/resetaConfigLib.sh"))
            return new ResponseEntity<>("Erro ao resetar as informações do dispositivo no código fonte", HttpStatus.INTERNAL_SERVER_ERROR);

		if(!ProcessoBash.runProcess("arduino-cli upload -b " + placa + " -p /dev/" + portaCadastro + " OTADefault")) 
			return new ResponseEntity<>("Erro ao fazer upload do código de cadastro com o arduino-cli", HttpStatus.INTERNAL_SERVER_ERROR);
		
        disp = dispRepo.save(disp);
        if(disp == null) return ResponseEntity.badRequest().body("Não foi possível criar o dispositivo");
        
        return ResponseEntity.ok("Dispositivo criado com sucesso");
    }
}
