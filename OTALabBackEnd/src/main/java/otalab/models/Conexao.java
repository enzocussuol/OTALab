package otalab.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Conexao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long idConexao;

    @ManyToOne
	@JoinColumn(name = "id")
    public Dispositivo dispositivo;

    @Column
    public String ip;

    public Conexao(){

    }

    public Conexao(Dispositivo dispositivo, String ip){
        this.dispositivo = dispositivo;
        this.ip = ip;
    }

    public long getIdConexao(){
        return this.idConexao;
    }

    public Dispositivo getDispositivo(){
        return this.dispositivo;
    }

    public void setDispositivo(Dispositivo dispositivo){
        this.dispositivo = dispositivo;
    }

    public String getIp(){
        return this.ip;
    }

    public void setIp(String ip){
        this.ip = ip;
    }
}
