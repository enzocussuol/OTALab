package otalab.admin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fazecast.jSerialComm.SerialPort;
import com.google.gson.Gson;

import otalab.util.Dispositivo;
import otalab.util.ProcessoBash;

@Controller
public class AdminController {
	Configuracao configuracao = new Configuracao();
	
	@GetMapping("/OTALabAdmin")
	public String getIndex(Model model, @RequestParam(defaultValue="") String status) {
		SerialPort[] portas = SerialPort.getCommPorts();
		ArrayList<String> nomesPortas = new ArrayList<String>();
		
		for(SerialPort porta: portas) {
			nomesPortas.add(porta.getSystemPortName());
		}
		
		model.addAttribute("status", status);
		model.addAttribute("novaConfiguracao", new Configuracao(true));
		model.addAttribute("configuracao", this.configuracao);
		model.addAttribute("senhaWiFi", this.configuracao.escondeSenha());
		model.addAttribute("dispositivo", new Dispositivo());
		model.addAttribute("portas", nomesPortas);
		
		return "admin";
	}
	
	@PostMapping("/handleConfiguracaoRede")
	public ResponseEntity<String> handleConfiguracaoRede(@ModelAttribute Configuracao novaConfiguracao, Model model) {
		this.configuracao = novaConfiguracao;
		
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(System.getProperty("user.home") + "/OTALab/configuracaoGeralRede.conf"));
			
			writer.write(novaConfiguracao.toString());
			
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Nao foi possivel encontrar o arquivo configuracaoGeralRede.conf");
			e.printStackTrace();
			
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		
		return new ResponseEntity<>("Sucesso", HttpStatus.OK);
	}
	
	@PostMapping("/handleRegistro")
	public ResponseEntity<String> handleRegistro(@ModelAttribute Dispositivo dispositivo, Model model) {
		System.out.println("\n\n\n\n\n\nIniciando cadastro do dispositivo...");
		System.out.println("Nome: " + dispositivo.getNome());
		
		System.out.println("Gerando arquivo .json do dispositivo...");
		
		String pathDispositivos = System.getProperty("user.home") + "/OTALab/Dispositivos";
		
		File diretorioDispositivos = new File(pathDispositivos);
		if(!diretorioDispositivos.exists()) diretorioDispositivos.mkdir();
		
		Gson gson = new Gson();
		FileWriter writer;
		try {
			writer = new FileWriter(pathDispositivos + "/" + dispositivo.getNome() + ".json");
			
			gson.toJson(dispositivo, writer);
			
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Nao foi possivel encontrar o arquivo " + dispositivo.getNome() + ".json");
			e.printStackTrace();
			
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		
		System.out.println("############# Arquivo gerado. #############");
		
		ArrayList<String> command = new ArrayList<String>();
		
		System.out.println("Injetando informacoes do dispositivo no codigo fonte...");
		
		String pathOTADefault= System.getProperty("user.home") + "/OTALab/OTADefault";
		
		File diretorioOTADefault = new File(pathOTADefault);
		if(!diretorioOTADefault.exists()) diretorioOTADefault.mkdir();
		
		command.add("cp");
		command.add("OTATemplate.ino");
		command.add("OTADefault/OTADefault.ino");
		if(!ProcessoBash.runProcess(command)) return new ResponseEntity<>("Erro na copia do template", HttpStatus.BAD_REQUEST);
		
		command.clear();
		command.add("bash");
		command.add("Scripts/injetaInformacoes.sh");
		command.add(this.configuracao.getNomeWiFi());
		command.add(this.configuracao.getSenhaWiFi());
		command.add(this.configuracao.getIpBroker());
		command.add(dispositivo.getNome());
		command.add("OTADefault/OTADefault.ino");
		if(!ProcessoBash.runProcess(command)) return new ResponseEntity<>("Erro ao injetar as informacoes do dispositivo no codigo fonte do usuario", HttpStatus.BAD_REQUEST);
		
		System.out.println("############# Informacoes injetadas. #############");
		
		System.out.println("Compilando codigo fonte via arduino-cli...");
		
		command.clear();
		command.add("arduino-cli");
		command.add("compile");
		command.add("-b");
		command.add(dispositivo.getPlaca());
		command.add("OTADefault");
		if(!ProcessoBash.runProcess(command)) return new ResponseEntity<>("Erro ao compilar o codigo do usuario com o arduino-cli", HttpStatus.BAD_REQUEST);
		
		System.out.println("############# Codigo compilado. #############");
		
		System.out.println("Enviando codigo fonte para o dispositivo via arduino-cli...");
		
		command.clear();
		command.add("arduino-cli");
		command.add("upload");
		command.add("-b");
		command.add(dispositivo.getPlaca());
		command.add("-p");
		command.add("/dev/" + dispositivo.getPorta());
		command.add("OTADefault");
		if(!ProcessoBash.runProcess(command)) return new ResponseEntity<>("Erro ao enviar ao fazer upload do codigo do usuario com o arduino-cli", HttpStatus.BAD_REQUEST);
		
		System.out.println("############# Codigo enviado. #############");
		System.out.println("############# Fim do cadastro. #############");
		
		return new ResponseEntity<>("Sucesso", HttpStatus.OK);
	}
}
