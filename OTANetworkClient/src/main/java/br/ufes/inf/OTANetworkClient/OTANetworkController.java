package br.ufes.inf.OTANetworkClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;

@Controller
public class OTANetworkController {
	private Catalogo catalogo = new Catalogo();
	
	@GetMapping("/OTANetworkClient")
	public String getIndex(Model model) {
		try {
			Path arquivoDispositivosAtivos = Path.of("/home/enzo/OTANetwork/Relatorios/ativos.json");
			String stringJSON = Files.readString(arquivoDispositivosAtivos);
			
			Gson gson = new Gson();
			this.catalogo = gson.fromJson(stringJSON, Catalogo.class);
		}catch(IOException e) {
			e.printStackTrace();
		}
		
		model.addAttribute("dispositivos", this.catalogo.dispositivos);
		
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
	
	@PostMapping("/upload")
	public ResponseEntity<?> handleFileUpload(@RequestParam("file") MultipartFile file,
											@RequestParam("idDispositivo") String idDispositivo) throws IOException, InterruptedException{
		System.out.println("Upload realizado!");
		
		String fileName = file.getOriginalFilename();
		String fileNameNoExtension = fileName.substring(0, fileName.indexOf('.'));
		
		System.out.println("Nome do arquivo recebido: " + fileName);
		System.out.println("Id do dispositivo selecionado: " + idDispositivo);
		
		System.out.println("Iniciando processo de envio de codigo...");
		
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
		command.add("bash");
		command.add("Scripts/enviaCodigo.sh");
		command.add(idDispositivo);
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
		
		System.out.println("Envio realizado com sucesso!");
		
		return ResponseEntity.ok("File Uploaded Successfully");
	}
}