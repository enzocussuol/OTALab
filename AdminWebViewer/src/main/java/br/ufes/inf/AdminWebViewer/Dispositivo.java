package br.ufes.inf.AdminWebViewer;

public class Dispositivo {
	private String nome;
	private String descricao;
	private String placa;
	private String porta;
	
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
	
	@Override
	public String toString() {
		String str = "";
		
		str += this.nome + "\n";
		str += this.descricao + "\n";
		str += "\n" + this.placa + "\n";
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
