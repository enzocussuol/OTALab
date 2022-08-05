package otalab.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Sensor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idSensor;

    @Column
    private String nome;

    @Column
    private String descricao;

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
}
