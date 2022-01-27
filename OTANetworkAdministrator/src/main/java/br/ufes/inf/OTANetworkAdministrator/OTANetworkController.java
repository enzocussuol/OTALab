package br.ufes.inf.OTANetworkAdministrator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

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
	
	@GetMapping("/OTANetworkAdmin")
	public String getIndex(Model model, @RequestParam(defaultValue="") String status) {
		SerialPort[] portas = SerialPort.getCommPorts();
		ArrayList<String> nomesPortas = new ArrayList<String>();
		
		for(SerialPort porta: portas) nomesPortas.add(porta.getSystemPortName());
		
		model.addAttribute("status", status);
		model.addAttribute("novaConfiguracao", new Configuracao(true));
		model.addAttribute("configuracao", this.configuracao);
		model.addAttribute("senhaWiFi", this.configuracao.escondeSenha());
		model.addAttribute("dispositivo", new Dispositivo());
		model.addAttribute("portas", nomesPortas);
		
		return "index";
	}
	
	@PostMapping("/handleConfiguracaoRede")
	public String handleConfiguracaoRede(@ModelAttribute Configuracao novaConfiguracao, Model model) throws IOException {
		this.configuracao = novaConfiguracao;
		BufferedWriter writer = new BufferedWriter(new FileWriter("/home/enzo/OTANetwork/configuracaoGeralRede.conf"));
		writer.write(novaConfiguracao.toString());
		
		writer.flush();
		writer.close();
		
		return this.getIndex(model, "Configuracoes atualizadas");
	}
	
	private boolean runProcess(ArrayList<String> command) throws IOException, InterruptedException {
		System.out.print("Rodando comando: ");
		for(int i = 0; i < command.size(); i++) System.out.print(command.get(i) + " ");
		System.out.println();
		
		ProcessBuilder processBuilder = new ProcessBuilder(command);
		processBuilder.directory(new File("/home/enzo/OTANetwork/"));
		
		Process process = processBuilder.start();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		
		String line;
		
		while((line = reader.readLine()) != null) {
			System.out.println(line);
		}
		
		int exitCode = process.waitFor();
		
		if(exitCode == 0) {
			System.out.println("Process succeeded (exit code: 0)");
			return true;
		}
		else {
			System.out.println("Process failed (exit code: " + exitCode + ")");
			System.out.println("Abortando...");
			return false;
		}
	}
	
	@PostMapping("/OTANetworkAdmin")
	public String handleRegistro(@ModelAttribute Dispositivo dispositivo, Model model) throws IOException, InterruptedException{
		StopWatch cronometroGeral = new StopWatch();
		StopWatch cronometroEspecifico = new StopWatch();
		
		System.out.println("\n\n\n\n\n\nIniciando cadastro do dispositivo...");
		System.out.println("Nome: " + dispositivo.getNome());
		cronometroGeral.start();
		
		System.out.println("Gerando arquivo .json do dispositivo...");
		cronometroEspecifico.start();
		
		Gson gson = new Gson();
		FileWriter writer = new FileWriter("/home/enzo/OTANetwork/Dispositivos/" + dispositivo.getNome() + ".json");
		
		gson.toJson(dispositivo, writer);
		
		writer.flush();
		writer.close();
		
		cronometroEspecifico.stop();
		System.out.println("############# Arquivo gerado. Tempo gasto (ms) = " + cronometroEspecifico.getLastTaskTimeMillis() + " #############");
		
		ArrayList<String> command = new ArrayList<String>();
		
		System.out.println("Injetando informacoes do dispositivo no codigo fonte...");
		cronometroEspecifico.start();
		
		command.add("cp");
		command.add("OTATemplate.ino");
		command.add("OTADefault/OTADefault.ino");
		if(!this.runProcess(command)) return this.getIndex(model, "Erro ao copiar o template");
		
		command.clear();
		command.add("bash");
		command.add("Scripts/injetaInformacoes.sh");
		command.add(this.configuracao.getNomeWiFi());
		command.add(this.configuracao.getSenhaWiFi());
		command.add(this.configuracao.getIpBroker());
		command.add(dispositivo.getNome());
		command.add("OTADefault/OTADefault.ino");
		if(!this.runProcess(command)) return this.getIndex(model, "Erro ao injetar informacoes no codigo padrao");
		
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
		if(!this.runProcess(command)) return this.getIndex(model, "Erro ao compilar o codigo com o arduino-cli");
		
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
		if(!this.runProcess(command)) return this.getIndex(model, "Erro ao enviar o codigo com o arduino-cli");
		
		cronometroEspecifico.stop();
		System.out.println("############# Codigo enviado. Tempo gasto (ms) = " + cronometroEspecifico.getLastTaskTimeMillis() + " #############");
		
		cronometroGeral.stop();
		System.out.println("############# Fim do cadastro. Tempo gasto (ms) = " + cronometroGeral.getTotalTimeMillis() + " #############");
		
		return this.getIndex(model, "Dispositivo cadastrado com sucesso");
	}
}
