package otalab.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Processo {
	private String stdOut;
	private String stdErr;
	private boolean exitCode;

	public Processo(){
		stdOut = "";
		stdErr = "";
		exitCode = true;
	}

	public String getStdOut() {
		return this.stdOut;
	}

	public String getStdErr() {
		return this.stdErr;
	}

	public boolean getExitCode() {
		return this.exitCode;
	}

	public void executa(String command){
		System.out.println(command);

		ArrayList<String> commandAsArray = new ArrayList<String>(Arrays.asList(command.trim().split("\\s+")));
		
		ProcessBuilder processBuilder = new ProcessBuilder(commandAsArray);
		processBuilder.directory(new File("src/main/resources"));
		
		Process process = null;
		try {
			process = processBuilder.start();
			stdOut = new String(process.getInputStream().readAllBytes());
			stdErr = new String(process.getErrorStream().readAllBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int exitCodeValue = 0;
		try {
			exitCodeValue = process.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if(exitCodeValue == 0) {
			System.out.println("Process succeeded (exit code: 0)");
			exitCode = true;
		}else {
			System.out.println("Process failed (exit code: " + exitCodeValue + ")");
			exitCode = false;
		}
	}
}
