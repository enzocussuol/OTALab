package otalab.util;

import com.google.gson.annotations.SerializedName;

public class Dispositivo {
	@SerializedName("nome")
	public String nome;
	@SerializedName("descricao")
	public String descricao;
	@SerializedName("placa")
	public String placa;
	@SerializedName("porta")
	public String porta;
	@SerializedName("id")
	public int id;
	@SerializedName("ip")
	public String ip;
	
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public String getPlaca() {
		return placa;
	}
	public void setPlaca(String placa) {
		this.placa = placa;
	}
	
	public String getPorta() {
		return porta;
	}
	public void setPorta(String porta) {
		this.porta = porta;
	}
	
	@Override
	public String toString() {
		return this.nome + " " + this.descricao + " " + this.placa + " " + this.porta + " " + this.id + " " + this.ip + "\n";
	}
}
