package br.ufes.inf.OTANetworkAdministrator;

import java.util.ArrayList;

public class Dispositivo {
	private String nome;
	private ArrayList<String> descricao = new ArrayList<String>();
	private String placa;
	private ArrayList<String> sensores = new ArrayList<String>();
	private String porta;
	
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public ArrayList<String> getDescricao() {
		return descricao;
	}
	public void setDescricao(ArrayList<String> descricao) {
		this.descricao = descricao;
	}
	public String getPlaca() {
		return placa;
	}
	public void setPlaca(String placa) {
		this.placa = placa;
	}
	public ArrayList<String> getSensores() {
		return sensores;
	}
	public void setSensores(ArrayList<String> sensores) {
		this.sensores = sensores;
	}
	
	@Override
	public String toString() {
		String str = "";
		
		str += this.nome + "\n";
		
		for(int i = 0; i < this.descricao.size(); i++) {
			str += this.descricao.get(i);
		}
		
		str += "\n" + this.placa + "\n";		
		
		for(int i = 0; i < this.sensores.size(); i++) {
			str += this.sensores.get(i) + " ";
		}
		
		str += "\n";
		
		return str;
	}
	public String getPorta() {
		return porta;
	}
	public void setPorta(String porta) {
		this.porta = porta;
	}
}
