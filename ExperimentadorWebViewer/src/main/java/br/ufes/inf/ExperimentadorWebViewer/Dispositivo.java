package br.ufes.inf.ExperimentadorWebViewer;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class Dispositivo {
	@SerializedName("nome")
	public String nome;
	@SerializedName("descricao")
	public String descricao;
	@SerializedName("placa")
	public String placa;
	@SerializedName("sensores")
	public ArrayList<String> sensores = new ArrayList<String>();
	@SerializedName("porta")
	public String porta;
	@SerializedName("id")
	public int id;
	@SerializedName("ip")
	public String ip;
	
	@Override
	public String toString() {
		return this.nome + " " + this.descricao + " " + this.placa + " " + this.sensores.toString() + " " + this.porta + " " + this.id + " " + this.ip + "\n";
	}
}
