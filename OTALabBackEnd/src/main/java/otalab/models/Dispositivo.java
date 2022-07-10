package otalab.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Dispositivo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long idDispositivo;

	@Column
	public String nome;

	@Column
	public String descricao;

	@Column
	public String placa;

	@Column
	public String portaCadastro;
	
	public Dispositivo(){

	}

	public Dispositivo(String nome, String descricao, String placa, String portaCadastro){
		this.nome = nome;
		this.descricao = descricao;
		this.placa = placa;
		this.portaCadastro = portaCadastro;
	}

	public long getId(){
		return this.idDispositivo;
	}

	public String getNome(){
		return this.nome;
	}

	public void setNome(String nome){
		this.nome = nome;
	}

	public String getDescricao(){
		return this.descricao;
	}

	public void setDescricao(String descricao){
		this.descricao = descricao;
	}

	public String getPlaca(){
		return this.placa;
	}

	public void setPlaca(String placa){
		this.placa = placa;
	}

	public String getPortaCadastro(){
		return this.portaCadastro;
	}

	public void setPortaCadastro(String portaCadastro){
		this.portaCadastro = portaCadastro;
	}
}
