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
public class Sensor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idSensor;

    @Column
    private String nome;

    @Column
    private String descricao;

	@OneToMany(
		mappedBy = "sensor",
		cascade = CascadeType.ALL,
		orphanRemoval = true
	)
	@JsonManagedReference
	private List<Servico> servicos = new ArrayList<>();

    public Sensor(){

    }

    public Sensor(String nome, String descricao){
        this.nome = nome;
        this.descricao = descricao;
    }

	public long getIdSensor() {
		return this.idSensor;
	}

	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public List<Servico> getServicos() {
		return this.servicos;
	}

	public void setServicos(List<Servico> servicos) {
		this.servicos = servicos;
	}
}
