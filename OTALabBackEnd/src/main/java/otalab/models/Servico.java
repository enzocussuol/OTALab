package otalab.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
public class Servico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idServico;

    @ManyToOne
    @JoinColumn(name = "idSensor")
    @JsonBackReference
    private Sensor sensor;

    @OneToMany(
        mappedBy = "servico",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    @JsonManagedReference
    private List<Pinagem> pinagens = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "idDispositivo")
    @JsonBackReference
    private Dispositivo dispositivo;

    public Servico(){

    }

    public Servico(Sensor sensor, Dispositivo dispositivo){
        this.sensor = sensor;
        this.dispositivo = dispositivo;
    }

    public long getIdServico() {
        return this.idServico;
    }

    public Sensor getSensor() {
        return this.sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    public List<Pinagem> getPinagens() {
        return this.pinagens;
    }

    public void setPinagens(List<Pinagem> pinagens) {
        this.pinagens = pinagens;
    }
}
