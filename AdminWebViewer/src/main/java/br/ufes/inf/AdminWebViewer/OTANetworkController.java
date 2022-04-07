package br.ufes.inf.AdminWebViewer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fazecast.jSerialComm.SerialPort;
import com.google.gson.Gson;

@Controller
public class OTANetworkController {
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
		
		return "index";
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
	
	private boolean runProcess(ArrayList<String> command) {
		System.out.print("Rodando comando: ");
		for(int i = 0; i < command.size(); i++) System.out.print(command.get(i) + " ");
		System.out.println();
		
		ProcessBuilder processBuilder = new ProcessBuilder(command);
		processBuilder.directory(new File(System.getProperty("user.home") + "/OTALab/"));
		
		Process process = null;
		try {
			process = processBuilder.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Nao foi possivel iniciar o processo no diretorio fornecido");
			e.printStackTrace();
		}
		
		BufferedReader reader;
		String line;
		
		try {
			reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			while((line = reader.readLine()) != null) {
				System.out.println(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Ocorreu um erro ao ler o resultado do processo");
			e.printStackTrace();
		}
		
		int exitCode = 0;
		try {
			exitCode = process.waitFor();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			System.out.println("Erro: o processo terminou com uma excessao de interrupcao");
			e.printStackTrace();
		}
		
		if(exitCode == 0) {
			System.out.println("Process succeeded (exit code: 0)");
			return true;
		}else {
			System.out.println("Process failed (exit code: " + exitCode + ")");
			System.out.println("Abortando...");
			return false;
		}
	}
	
	@PostMapping("/handleRegistro")
	public ResponseEntity<String> handleRegistro(@ModelAttribute Dispositivo dispositivo, Model model) {
		StopWatch cronometroGeral = new StopWatch();
		StopWatch cronometroEspecifico = new StopWatch();
		
		System.out.println("\n\n\n\n\n\nIniciando cadastro do dispositivo...");
		System.out.println("Nome: " + dispositivo.getNome());
		cronometroGeral.start();
		
		System.out.println("Gerando arquivo .json do dispositivo...");
		cronometroEspecifico.start();
		
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
		
		cronometroEspecifico.stop();
		System.out.println("############# Arquivo gerado. Tempo gasto (ms) = " + cronometroEspecifico.getLastTaskTimeMillis() + " #############");
		
		ArrayList<String> command = new ArrayList<String>();
		
		System.out.println("Injetando informacoes do dispositivo no codigo fonte...");
		cronometroEspecifico.start();
		
		command.add("cp");
		command.add("OTATemplate.ino");
		command.add("OTADefault/OTADefault.ino");
		if(!this.runProcess(command)) return new ResponseEntity<>("Erro na copia do template", HttpStatus.BAD_REQUEST);
		
		command.clear();
		command.add("bash");
		command.add("Scripts/injetaInformacoes.sh");
		command.add(this.configuracao.getNomeWiFi());
		command.add(this.configuracao.getSenhaWiFi());
		command.add(this.configuracao.getIpBroker());
		command.add(dispositivo.getNome());
		command.add("OTADefault/OTADefault.ino");
		if(!this.runProcess(command)) return new ResponseEntity<>("Erro ao injetar as informacoes do dispositivo no codigo fonte do usuario", HttpStatus.BAD_REQUEST);
		
		cronometroEspecifico.stop();
		System.out.println("############# Informacoes injetadas. Tempo gasto (ms) = " + cronometroEspecifico.getLastTaskTimeMillis() + " #############");
		
		System.out.println("Compilando codigo fonte via arduino-cli...");
		cronometroEspecifico.start();
		
		command.clear();
		command.add("arduino-cli");
		command.add("compile");
		command.add("-b");
		command.add(dispositivo.getPlaca());
		command.add("OTADefault");
		if(!this.runProcess(command)) return new ResponseEntity<>("Erro ao compilar o codigo do usuario com o arduino-cli", HttpStatus.BAD_REQUEST);
		
		cronometroEspecifico.stop();
		System.out.println("############# Codigo compilado. Tempo gasto (ms) = " + cronometroEspecifico.getLastTaskTimeMillis() + " #############");
		
		System.out.println("Enviando codigo fonte para o dispositivo via arduino-cli...");
		cronometroEspecifico.start();
		
		command.clear();
		command.add("arduino-cli");
		command.add("upload");
		command.add("-b");
		command.add(dispositivo.getPlaca());
		command.add("-p");
		command.add("/dev/" + dispositivo.getPorta());
		command.add("OTADefault");
		if(!this.runProcess(command)) return new ResponseEntity<>("Erro ao enviar ao fazer upload do codigo do usuario com o arduino-cli", HttpStatus.BAD_REQUEST);
		
		cronometroEspecifico.stop();
		System.out.println("############# Codigo enviado. Tempo gasto (ms) = " + cronometroEspecifico.getLastTaskTimeMillis() + " #############");
		
		cronometroGeral.stop();
		System.out.println("############# Fim do cadastro. Tempo gasto (ms) = " + cronometroGeral.getTotalTimeMillis() + " #############");
		
		return new ResponseEntity<>("Sucesso", HttpStatus.OK);
	}
}
