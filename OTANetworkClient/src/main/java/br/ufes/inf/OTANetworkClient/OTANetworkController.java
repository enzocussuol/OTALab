package br.ufes.inf.OTANetworkClient;

import java.io.File;

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
	
	@PostMapping("/upload")
	public ResponseEntity<?> handleFileUpload(@RequestParam("file") MultipartFile file){
		String fileName = file.getOriginalFilename();
		
		try {
			file.transferTo(new File("/home/enzo/OTANetwork/Uploads/" + fileName));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		
		return ResponseEntity.ok("File Uploaded Successfully");
	}
}
