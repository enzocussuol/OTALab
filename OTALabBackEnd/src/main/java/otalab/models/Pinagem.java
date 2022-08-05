package otalab.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
public class Pinagem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idPinagem;

    @Column
    private String pinoDispositivo;

    @Column
    private String pinoSensor;

    @ManyToOne
	@JoinColumn(name = "idServico")
    @JsonBackReference
    private Servico servico;

    public Pinagem(){

    }

    public Pinagem(String pinoDispositivo, String pinoSensor, Servico servico){
        this.pinoDispositivo = pinoDispositivo;
        this.pinoSensor = pinoSensor;
        this.servico = servico;
    }

	public long getIdPinagem() {
		return this.idPinagem;
	}

	public String getPinoDispositivo() {
		return this.pinoDispositivo;
	}

	public void setPinoDispositivo(String pinoDispositivo) {
		this.pinoDispositivo = pinoDispositivo;
	}

	public String getPinoSensor() {
		return this.pinoSensor;
	}

	public void setPinoSensor(String pinoSensor) {
		this.pinoSensor = pinoSensor;
	}

    public Servico getServico() {
        return this.servico;
    }

    public void setServico(Servico servico) {
        this.servico = servico;
    }
}
