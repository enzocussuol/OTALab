package br.ufes.inf.ExperimentadorWebViewer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;

@Controller
public class OTANetworkController {
	private Catalogo catalogo = new Catalogo();
	
	@GetMapping("/OTALabExperimentador")
	public String getIndex(Model model) {
		try {
			Path arquivoDispositivosAtivos = Path.of(System.getProperty("user.home") + "/OTALab/Relatorios/ativos.json");
			String stringJSON = Files.readString(arquivoDispositivosAtivos);
			
			Gson gson = new Gson();
			this.catalogo = gson.fromJson(stringJSON, Catalogo.class);
		}catch(IOException e) {
			System.out.println("Nao foi possivel encontrar o arquivo ativos.json");
			this.catalogo = new Catalogo();
		}
		
		model.addAttribute("dispositivos", this.catalogo.dispositivos);
		
		return "index";
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
	
	@PostMapping("/handleAtualizaDispositivos")
	public ResponseEntity<?> handleAtualizaDispositivos(Model model) {
		Scanner scanner;
		try {
			scanner = new Scanner(new File(System.getProperty("user.home") + "/OTALab/configuracaoGeralRede.conf"));
			
			if(scanner.hasNextLine()) {
				scanner.nextLine();
				scanner.nextLine();
				
				ArrayList<String> command = new ArrayList<String>();
				
				command.add("bash");
				command.add("Scripts/atualizaDispositivosVivos.sh");
				command.add(scanner.nextLine());
				scanner.close();
				
				this.runProcess(command); // Por algum motivo o script python descobreDispositivos.py nos da exit code != 0... Por enquanto considerar que o script nao falhou!
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("Nao foi possivel encontrar o arquivo configuracaoGeralRede.conf");
			e.printStackTrace();
		}
		
		return ResponseEntity.ok("Ok");
	}
	
	@PostMapping("/handleFileUpload")
	public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file,
											@RequestParam("idDispositivo") String idDispositivo,
											@RequestParam("nomeDispositivo") String nomeDispositivo) {
		StopWatch cronometroGeral = new StopWatch();
		StopWatch cronometroEspecifico = new StopWatch();
		
		System.out.println("\n\n\n\n\n\nCodigo fonte recebido. Iniciando processo de upload para o dispositivo...");
		cronometroGeral.start();
		
		String fileName = file.getOriginalFilename();
		String fileNameNoExtension = fileName.substring(0, fileName.indexOf('.'));
		
		System.out.println("Nome do arquivo recebido: " + fileName);
		System.out.println("Id do dispositivo selecionado: " + idDispositivo);
		
		System.out.println("Iniciando processo de envio de codigo...");
		
		String pathUploads = System.getProperty("user.home") + "/OTALab/Uploads";
		
		File diretorioUploads = new File(pathUploads);
		if(!diretorioUploads.exists()) diretorioUploads.mkdir();
		
		try {
			file.transferTo(new File(pathUploads + "/" + fileName));
		} catch (Exception e) {
			System.out.println("Nao foi possivel fazer a transferencia do arquivo " + fileName);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		
		ArrayList<String> command = new ArrayList<String>();
		
		System.out.println("Injetando informacoes do dispositivo no codigo fonte...");
		cronometroEspecifico.start();
		
		command.add("bash");
		command.add("Scripts/injetaInformacoes.sh");
		
		Scanner scanner;
		try {
			scanner = new Scanner(new File(System.getProperty("user.home") + "/OTALab/configuracaoGeralRede.conf"));
			
			command.add(scanner.nextLine());
			command.add(scanner.nextLine());
			command.add(scanner.nextLine());
			scanner.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("Nao foi possivel encontrar o arquivo configuracaoGeralRede.conf");
			e.printStackTrace();
		}
		
		command.add(nomeDispositivo);
		command.add("Uploads/" + fileName);
		if(!this.runProcess(command)) return new ResponseEntity<>("Falha ao injetar informacoes no dispositivo", HttpStatus.BAD_REQUEST);
		
		cronometroEspecifico.stop();
		System.out.println("############# Informacoes injetadas. Tempo gasto (ms) = " + cronometroEspecifico.getLastTaskTimeMillis() + " #############");
		
		command.clear();
		command.add("cp");
		command.add("Uploads/" + fileName);
		command.add(".");
		if(!this.runProcess(command)) return new ResponseEntity<>("Falha ao copiar arquivo da pasta de uploads para o lugar correto", HttpStatus.BAD_REQUEST);
		
		System.out.println("Enviando codigo fonte via OTA para o dispositivo...");
		cronometroEspecifico.start();
		
		command.clear();
		command.add("bash");
		command.add("Scripts/enviaCodigo.sh");
		command.add(idDispositivo);
		command.add(fileNameNoExtension);
		if(!this.runProcess(command)) return new ResponseEntity<>("Falha ao enviar codigo para o dispositivo (erro de compilacao ou envio)", HttpStatus.BAD_REQUEST);
		
		cronometroEspecifico.stop();
		System.out.println("############# Envio realizado. Tempo gasto (ms) = " + cronometroEspecifico.getLastTaskTimeMillis() + " #############");
		
		command.clear();
		command.add("rm");
		command.add(fileName);
		this.runProcess(command);

		command.clear();
		command.add("rm");
		command.add("-r");
		command.add(fileNameNoExtension);
		this.runProcess(command);
		
		cronometroGeral.stop();
		System.out.println("############# Fim do envio. Tempo gasto (ms) = " + cronometroGeral.getTotalTimeMillis() + " #############");
		
		return new ResponseEntity<>("Sucesso", HttpStatus.OK);
	}
}
