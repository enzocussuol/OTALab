package br.ufes.inf.OTANetworkAdministrator;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.fazecast.jSerialComm.SerialPort;

@Controller
public class OTANetworkController {
	@GetMapping("/OTANetworkAdmin")
	public String getIndex(Model model) {
		SerialPort[] portas = SerialPort.getCommPorts();
		ArrayList<String> nomesPortas = new ArrayList<String>();
		
		for(SerialPort porta: portas) nomesPortas.add(porta.getSystemPortName());
		
		model.addAttribute("dispositivo", new Dispositivo());
		model.addAttribute("portas", nomesPortas);
		
		return "index";
	}
	
	private void runProcess(ArrayList<String> command) throws IOException, InterruptedException {
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
		
		if(exitCode == 0) System.out.println("Process succeeded (exit code: 0)");
		else {
			System.out.println("Process failed (exit code: " + exitCode + ")");
			System.exit(0);
		}
	}
	
	@PostMapping("/OTANetworkAdmin")
	public String handleRegistro(@ModelAttribute Dispositivo dispositivo, Model model) throws IOException, InterruptedException{
		System.out.println("Iniciando cadastro do dispositivo...");
		
		ArrayList<String> command = new ArrayList<String>();
		
		command.add("arduino-cli");
		command.add("compile");
		command.add("-b");
		command.add(dispositivo.getPlaca());
		command.add("OTADefault");
		this.runProcess(command);
		
		command.clear();
		command.add("arduino-cli");
		command.add("upload");
		command.add("-b");
		command.add(dispositivo.getPlaca());
		command.add("-p");
		command.add("/dev/" + dispositivo.getPorta());
		command.add("OTADefault");
		this.runProcess(command);
		
		return this.getIndex(model);
	}
}
