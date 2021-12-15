package br.ufes.inf.OTANetworkAdministrator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Configuracao {
	private String nomeWiFi;
	private String senhaWiFi;
	private String ipBroker;
	
	public Configuracao() {
		try {
			Scanner scanner = new Scanner(new File("/home/enzo/OTANetwork/configuracaoGeralRede.conf"));
			
			this.nomeWiFi = scanner.next();
			this.senhaWiFi = scanner.next();
			this.ipBroker = scanner.next();
			
			scanner.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Configuracao(boolean novaConfiguracao) {
		
	}
	
	public String getNomeWiFi() {
		return nomeWiFi;
	}
	public void setNomeWiFi(String nomeWiFi) {
		this.nomeWiFi = nomeWiFi;
	}
	public String getSenhaWiFi() {
		return senhaWiFi;
	}
	public void setSenhaWiFi(String senhaWiFi) {
		this.senhaWiFi = senhaWiFi;
	}
	public String getIpBroker() {
		return ipBroker;
	}
	public void setIpBroker(String ipBroker) {
		this.ipBroker = ipBroker;
	}
	
	@Override
	public String toString() {
		return this.nomeWiFi + "\n" + this.senhaWiFi + "\n" + this.ipBroker;
	}
}
