package br.ufes.inf.OTANetworkClient;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class Catalogo {
	@SerializedName("dispositivos")
	public ArrayList<Dispositivo> dispositivos = new ArrayList<Dispositivo>();
	
	@Override
	public String toString() {
		String str = "";
		
		for(Dispositivo dispositivo: this.dispositivos) {
			str += dispositivo.toString();
		}
		
		return str;
	}
}
