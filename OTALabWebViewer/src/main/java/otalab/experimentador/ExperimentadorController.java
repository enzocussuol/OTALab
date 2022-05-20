package otalab.experimentador;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;

import otalab.util.ProcessoBash;

@Controller
public class ExperimentadorController {
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
		
		return "experimentador";
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
				
				ProcessoBash.runProcess(command); // Por algum motivo o script python descobreDispositivos.py nos da exit code != 0... Por enquanto considerar que o script nao falhou!
			}
		} catch (Exception e) {
			return new ResponseEntity<>("Arquivo de configuracao da rede nao encontrado ou mal formatado!", HttpStatus.BAD_REQUEST);
		}
		
		return ResponseEntity.ok("Ok");
	}
	
	@PostMapping("/handleFileUpload")
	public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file,
											@RequestParam("idDispositivo") String idDispositivo,
											@RequestParam("nomeDispositivo") String nomeDispositivo) {
		System.out.println("\n\n\n\n\n\nCodigo fonte recebido. Iniciando processo de upload para o dispositivo...");
		
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
		if(!ProcessoBash.runProcess(command)) return new ResponseEntity<>("Falha ao injetar informacoes no dispositivo", HttpStatus.BAD_REQUEST);
		
		System.out.println("############# Informacoes injetadas. #############");
		
		command.clear();
		command.add("cp");
		command.add("Uploads/" + fileName);
		command.add(".");
		if(!ProcessoBash.runProcess(command)) return new ResponseEntity<>("Falha ao copiar arquivo da pasta de uploads para o lugar correto", HttpStatus.BAD_REQUEST);
		
		System.out.println("Enviando codigo fonte via OTA para o dispositivo...");
		
		command.clear();
		command.add("bash");
		command.add("Scripts/enviaCodigo.sh");
		command.add(idDispositivo);
		command.add(fileNameNoExtension);
		if(!ProcessoBash.runProcess(command)) return new ResponseEntity<>("Falha ao enviar codigo para o dispositivo (erro de compilacao ou envio)", HttpStatus.BAD_REQUEST);
		
		System.out.println("############# Envio realizado. #############");
		
		command.clear();
		command.add("rm");
		command.add(fileName);
		ProcessoBash.runProcess(command);

		command.clear();
		command.add("rm");
		command.add("-r");
		command.add(fileNameNoExtension);
		ProcessoBash.runProcess(command);
		
		System.out.println("############# Fim do envio. #############");
		
		return new ResponseEntity<>("Sucesso", HttpStatus.OK);
	}
}
