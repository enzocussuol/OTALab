package otalab.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public class ProcessoBash {
	public static boolean runProcess(String command){
		System.out.println("Executando comando:");
		System.out.println(command);

		ArrayList<String> commandAsArray = new ArrayList<String>(Arrays.asList(command.trim().split("\\s+")));
		
		ProcessBuilder processBuilder = new ProcessBuilder(commandAsArray);
		processBuilder.directory(new File(System.getProperty("user.home") + "/OTALab/"));
		
		Process process = null;
		try {
			process = processBuilder.start();
		} catch (IOException e) {
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
			System.out.println("Ocorreu um erro ao ler o resultado do processo");
			e.printStackTrace();
		}
		
		int exitCode = 0;
		try {
			exitCode = process.waitFor();
		} catch (InterruptedException e) {
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
}
