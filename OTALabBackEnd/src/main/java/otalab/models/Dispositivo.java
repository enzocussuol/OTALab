package otalab.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
public class Dispositivo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long idDispositivo;

	@Column
	private String nome;

	@Column
	private String descricao;

	@Column
	private String placa;

	@Column
	private String portaCadastro;

	@Column
	private String firmware;

	@OneToMany(
		mappedBy = "dispositivo",
		cascade = CascadeType.ALL,
		orphanRemoval = true
	)
	@JsonManagedReference
	private List<Servico> servicos = new ArrayList<>();

	@OneToMany(
		mappedBy = "dispositivo",
		cascade = CascadeType.ALL,
		orphanRemoval = true
	)
	@JsonManagedReference
	private List<Conexao> conexoes = new ArrayList<>();
	
	public Dispositivo(){

	}

	public Dispositivo(String nome, String descricao, String placa, String portaCadastro){
		this.nome = nome;
		this.descricao = descricao;
		this.placa = placa;
		this.portaCadastro = portaCadastro;
		this.firmware = "OTADefault.ino";
	}

	public long getIdDispositivo(){
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

	public String getFirmware() {
		return this.firmware;
	}

	public void setFirmware(String firmware) {
		this.firmware = firmware;
	}

	public List<Servico> getServicos() {
		return this.servicos;
	}

	public void setServicos(List<Servico> servicos) {
		this.servicos = servicos;
	}

	public List<Conexao> getConexoes() {
		return this.conexoes;
	}

	public void setConexoes(List<Conexao> conexoes) {
		this.conexoes = conexoes;
	}
}
