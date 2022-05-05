package otalab.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ProcessoBash {
	public static boolean runProcess(ArrayList<String> command) {
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
}
