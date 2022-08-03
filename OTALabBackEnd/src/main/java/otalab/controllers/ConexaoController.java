package otalab.controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import otalab.models.Conexao;
import otalab.models.Configuracao;
import otalab.models.Dispositivo;
import otalab.repo.ConexaoRepo;
import otalab.repo.ConfiguracaoRepo;
import otalab.repo.DispositivoRepo;
import otalab.util.ProcessoBash;


@RestController
public class ConexaoController {
    @Autowired
    ConexaoRepo conexaoRepo;

    @Autowired
    ConfiguracaoRepo configRepo;

    @Autowired
    DispositivoRepo dispRepo;

    @Value("${file.upload-dir}")
    String FILE_DIRECTORY;

    @GetMapping("/conexoes/read")
    public List<Conexao> readConexoes(){
        return conexaoRepo.findAll();
    }

    @GetMapping("/conexoes/read/{idConexao}")
    public ResponseEntity<Conexao> readConexaoById(@PathVariable long idConexao){
        Conexao conexao = conexaoRepo.findById(idConexao).orElse(null);

        if(conexao == null) return ResponseEntity.badRequest().body(null);
        return ResponseEntity.ok(conexao);
    }

    @PutMapping("/conexoes/update")
    public ResponseEntity<String> updateConexoes(int segundosEsperaRespostas, long idConfiguracao) throws MqttSecurityException, MqttException, InterruptedException{
        List<Conexao> conexoes = conexaoRepo.findAll();
        for(Conexao conexao: conexoes) conexaoRepo.delete(conexao);

        Configuracao config = configRepo.findById(idConfiguracao).orElse(null);
        if(config == null) return ResponseEntity.badRequest().body("Configuração não encontrada");

        String clientId = UUID.randomUUID().toString();
        IMqttClient client = new MqttClient("tcp://" + config.getIpBroker(), clientId);

        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(10);
        client.connect(options);

        client.subscribe("Inicializacao/inTopic", (topic, msg) -> {
            String payload = new String(msg.getPayload());

            String ip = payload.substring(0, payload.indexOf(" "));
            long idDispositivo = Long.valueOf(payload.substring(payload.indexOf(" ") + 1, payload.length()));

            Dispositivo disp = dispRepo.findById(idDispositivo).orElse(null);

            if(disp != null){
                Conexao conexao = new Conexao(disp, ip);
                conexaoRepo.save(conexao);
            }
        });

        client.publish("Inicializacao/outTopic", new MqttMessage("Are you alive?".getBytes()));

        Thread.sleep(segundosEsperaRespostas * 1000);
        client.disconnect();
        client.close();

        return ResponseEntity.ok("Conexões atualizadas com sucesso");
    }

    @PostMapping(value = "/conexoes/upload/{idConexao}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> uploadToConexao(@PathVariable long idConexao, @RequestPart("file") MultipartFile file) throws IOException{
        Conexao conexao = conexaoRepo.findById(idConexao).orElse(null);
        if(conexao == null) return ResponseEntity.badRequest().body("Conexão não encontrada");

        String fileName = file.getOriginalFilename();
		String fileNameNoExtension = fileName.substring(0, fileName.indexOf('.'));

        File myFile = new File(FILE_DIRECTORY + fileName);
        myFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(myFile);
        fos.write(file.getBytes());
        fos.close();

        String ip = conexao.getIp();
        String placa = conexao.getDispositivo().getPlaca();

        if(!ProcessoBash.runProcess("bash scripts/enviaCodigo.sh " + ip + " " + placa + " " + fileNameNoExtension)) return new ResponseEntity<>("Erro ao realizar o upload do código enviado à conexão selecionada", HttpStatus.INTERNAL_SERVER_ERROR);

        return ResponseEntity.ok("Arquivo enviado com sucesso");
    }
}
