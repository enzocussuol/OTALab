package otalab.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Configuracao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long idConfiguracao;

    @Column
    public String nomeWiFi;

    @Column
	public String senhaWiFi;

    @Column
	public String ipBroker;

    @Column
    public boolean ativa;

    public Configuracao(){

    }

    public Configuracao(String nomeWiFi, String senhaWiFi, String ipBroker){
        this.nomeWiFi = nomeWiFi;
        this.senhaWiFi = senhaWiFi;
        this.ipBroker = ipBroker;
        this.ativa = false;
    }

    public long getIdConfiguracao(){
        return this.idConfiguracao;
    }

    public String getNomeWiFi(){
        return this.nomeWiFi;
    }

    public void setNomeWiFi(String nomeWiFi){
        this.nomeWiFi = nomeWiFi;
    }

    public String getSenhaWiFi(){
        return this.senhaWiFi;
    }

    public void setSenhaWiFi(String senhaWiFi){
        this.senhaWiFi = senhaWiFi;
    }

    public String getIpBroker(){
        return this.ipBroker;
    }

    public void setIpBroker(String ipBroker){
        this.ipBroker = ipBroker;
    }

    public boolean getAtiva(){
        return this.ativa;
    }

    public void setAtiva(boolean ativa){
        this.ativa = ativa;
    }
}
