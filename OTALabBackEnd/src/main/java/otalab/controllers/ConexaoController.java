package otalab.controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.UUID;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
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

import io.swagger.v3.oas.annotations.Operation;
import otalab.models.Conexao;
import otalab.models.Configuracao;
import otalab.models.Dispositivo;
import otalab.repo.ConexaoRepo;
import otalab.repo.ConfiguracaoRepo;
import otalab.repo.DispositivoRepo;
import otalab.util.Processo;

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

    @Operation(summary = "Lê todas as conexões existentes no momento.")
    @GetMapping("/conexoes/read")
    public List<Conexao> readConexoes(){
        return conexaoRepo.findAll();
    }

    @Operation(summary = "Lê uma conexão com um dado id.")
    @GetMapping("/conexoes/read/{idConexao}")
    public ResponseEntity<Conexao> readConexaoById(@PathVariable long idConexao){
        Conexao conexao = conexaoRepo.findById(idConexao).orElse(null);

        if(conexao == null) return ResponseEntity.badRequest().body(null);
        return ResponseEntity.ok(conexao);
    }

    @Operation(summary = "Atualiza a lista de conexões a partir do protocolo MQTT.")
    @PutMapping("/conexoes/update")
    public ResponseEntity<String> updateConexoes(int segundosEsperaRespostas, long idConfiguracao) throws MqttSecurityException, MqttException, InterruptedException{
        
        conexaoRepo.deleteAll();
	    System.out.println("Starting ...");
        Configuracao config = configRepo.findById(idConfiguracao).orElse(null);
        if(config == null) return ResponseEntity.badRequest().body("Configuração não encontrada");

        String clientId = UUID.randomUUID().toString();
        System.out.println("clientId: "+clientId);
        MemoryPersistence persistence = new MemoryPersistence();
        IMqttClient client = new MqttClient("tcp://" + config.getIpBroker(), clientId, persistence);
        
	    System.out.println("client: "+client);	
        MqttConnectOptions options = new MqttConnectOptions();
        System.out.println("options: "+options);
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(10);
        client.connect(options);
	    System.out.println("client.subscribe ...");

		client.subscribe("Inicializacao/inTopic", (topic, msg) -> {
		    String payload = new String(msg.getPayload());
		    System.err.println("payload: "+payload);

		    String ip = payload.substring(0, payload.indexOf(" "));
		    System.err.println("ip: "+ip);
		    long idDispositivo = Long.valueOf(payload.substring(payload.indexOf(" ") + 1, payload.length()));
		    System.err.println("idDispositivo: "+idDispositivo);
		    Dispositivo disp = dispRepo.findById(idDispositivo).orElse(null);
		    System.out.println("disp: "+disp);
		    if(disp != null){
                System.err.println("Entrou no IF");
                Conexao conexao = new Conexao(disp, ip);
                conexaoRepo.save(conexao);
		    }
		});

        client.publish("Inicializacao/outTopic", new MqttMessage("Are you alive?".getBytes()));
        Thread.sleep(segundosEsperaRespostas * 1000);
        client.disconnect();
        client.close();
	    System.out.println("FIM ...");
        return ResponseEntity.ok("Conexões atualizadas com sucesso");
    }

    @Operation(summary = "Realiza o upload de um código-fonte que implementa a biblioteca OTALabDevice para uma conexão via Over-The-Air.")
    @PostMapping(value = "/conexoes/upload/{idConexao}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> uploadToConexao(@PathVariable long idConexao, @RequestPart("file") MultipartFile file) throws IOException{
        Conexao conexao = conexaoRepo.findById(idConexao).orElse(null);
        if(conexao == null) return ResponseEntity.badRequest().body("Conexão não encontrada");

        Processo processo = new Processo();
	    processo.executa("mkdir uploads");
        
        String fileName = file.getOriginalFilename();
	    String fileNameNoExtention = fileName.substring(0, fileName.indexOf('.'));
        
        
        File myFile = new File(FILE_DIRECTORY + fileName);
        myFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(myFile);
        fos.write(file.getBytes());
        fos.close();

        String ip = conexao.getIp();
        Dispositivo disp = conexao.getDispositivo();

        processo.executa("mkdir " + fileNameNoExtention);
        processo.executa("mkdir " + fileNameNoExtention + "/build");
        processo.executa("cp uploads/" + fileName + " " + fileNameNoExtention + "/");

        String dir = System.getProperty("user.dir");

        processo.executa("arduino-cli compile --fqbn " + disp.getPlaca() + " " + fileNameNoExtention + " --build-path " + dir + "/src/main/resources/" + fileNameNoExtention + "/build");
        if(!processo.getExitCode()) return new ResponseEntity<>("Erro ao compilar o código submetido com o arduino-cli:\n\n" +
                                                                processo.getStdErr(), HttpStatus.INTERNAL_SERVER_ERROR);

        processo.executa("mv " + fileNameNoExtention + "/build/" + fileName + ".bin " + fileNameNoExtention + "/");

        processo.executa("python3 scripts/espota.py -d -i " + ip + " -P 20000 -f " + fileNameNoExtention + "/" + fileName + ".bin");
        if(!processo.getExitCode()) return new ResponseEntity<>("Erro ao enviar o código submetido via OTA:\n\n" +
                                                                processo.getStdErr(), HttpStatus.INTERNAL_SERVER_ERROR);

        processo.executa("rm uploads/" + fileName);
        processo.executa("rm -r " + fileNameNoExtention);

        disp.setFirmware(fileName);
        dispRepo.save(disp);

        return ResponseEntity.ok("Arquivo enviado com sucesso");
    }
}
