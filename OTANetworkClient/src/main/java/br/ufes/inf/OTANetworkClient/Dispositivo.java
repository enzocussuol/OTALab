package br.ufes.inf.OTANetworkClient;

import com.google.gson.annotations.SerializedName;

public class Dispositivo {
	@SerializedName("nome")
	public String nome;
	@SerializedName("placa")
	public String placa;
	@SerializedName("ip")
	public String ip;
	@SerializedName("id")
	public int id;
	
	@Override
	public String toString() {
		return this.nome + " " + this.placa + " " + this.ip + " " + this.id + "\n";
	}
}
