package br.ufes.inf.OTANetworkClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class OTANetworkController {
	@GetMapping("/OTANetwork")
	public String getIndex() {
		return "index";
	}
	
	private void runProcess(ArrayList<String> command) throws IOException, InterruptedException {
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
	
	@PostMapping("/upload")
	public ResponseEntity<?> handleFileUpload(@RequestParam("file") MultipartFile file) throws IOException, InterruptedException{
		String fileName = file.getOriginalFilename();
		String fileNameNoExtension = fileName.substring(0, fileName.indexOf('.'));
		System.out.println(fileNameNoExtension);
		
		try {
			file.transferTo(new File("/home/enzo/OTANetwork/Uploads/" + fileName));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		
		ArrayList<String> command = new ArrayList<String>();
		
		command.add("cp");
		command.add("Uploads/" + fileName);
		command.add(".");
		this.runProcess(command);
		
		command.clear();
		command.add("python3");
		command.add("Scripts/descobreDispositivos.py");
		command.add("127.0.0.1"); // IP DO BROKER UTILIZADO (CONFIGURACAO)
		this.runProcess(command);
		
		command.clear();
		command.add("bash");
		command.add("Scripts/difereAtivosInativos.sh");
		this.runProcess(command);
		
		command.clear();
		command.add("bash");
		command.add("Scripts/enviaCodigo.sh");
		command.add("0"); // ID DO DISPOSITIVO PARA QUAL SERA ENVIADO O CODIGO (CONFIGURACAO)
		command.add(fileNameNoExtension);
		this.runProcess(command);
		
		command.clear();
		command.add("rm");
		command.add(fileName);
		this.runProcess(command);

		command.clear();
		command.add("rm");
		command.add("-r");
		command.add(fileNameNoExtension);
		this.runProcess(command);
		
		return ResponseEntity.ok("File Uploaded Successfully");
	}
}
