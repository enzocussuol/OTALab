package otalab.controllers;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import otalab.models.Conexao;
import otalab.repo.ConexaoRepo;
import otalab.repo.ConfiguracaoRepo;
import otalab.util.ProcessoBash;

@RestController
public class ConexaoController {
    @Autowired
    ConexaoRepo conexaoRepo;

    @Autowired
    ConfiguracaoRepo configRepo;

    @GetMapping("/conexoes/read")
    public List<Conexao> readConexoes(){
        return conexaoRepo.findAll();
    }

    @GetMapping("/conexoes/read/{idConexao}")
    public ResponseEntity<Conexao> readConexaoById(@PathVariable long idConexao){
        Conexao conexao = conexaoRepo.getById(idConexao);

        if(conexao == null) return ResponseEntity.badRequest().body(null);
        return ResponseEntity.ok(conexao);
    }

    @PutMapping("/conexoes/update")
    public ResponseEntity<String> updateConexoes(int segundosEsperaRespostas, long idConfiguracao) throws MqttException{
        /*
            0 - Testar se as conexoes existentes ainda estao vivas (se nao, exclui-las)
            1 - Publicar no topico MQTT e receber todos os IPs dos dispositivos vivos
            2 - Pingar em cada um desses IPs e receber os nomes dos dispositivos
            3 - A partir do nome, criar uma conexao
            IDEAL: Dispositivos retornarem mais informacoes alem do ip a partir do MQTT!
        */
        List<Conexao> conexoes = conexaoRepo.findAll();
        for(Conexao conexao: conexoes){
            if(!ProcessoBash.runProcess("ping -c 1 " + conexao.getIp())){
                conexaoRepo.delete(conexao);
            }
        }

        List<String> respostas = Arrays.asList();

        try (IMqttClient client = new MqttClient("tcp://" + configRepo.getById(idConfiguracao).getIpBroker(), UUID.randomUUID().toString())) {
            client.connect();

            client.subscribe("Inicializacao/inTopic", (topic, msg) -> {
                byte[] payload = msg.getPayload();
                respostas.add(payload.toString());
            });

            client.publish("Inicializacao/outTopic", new MqttMessage("Are you alive?".getBytes()));

            try {
                Thread.sleep(segundosEsperaRespostas * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return new ResponseEntity<>("Erro na espera por respostas dos dispositivos", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            client.disconnect();
        }

        for(String resposta: respostas){
            System.out.println(resposta);
        }

        return ResponseEntity.ok("Conexões atualizadas com sucesso");
    }
}
