package br.com.agenda.model;

import java.io.Serializable;
public class Telefone implements Serializable{
	
	private static final long serialVersionUID = 5356158667653306530L;
	private String telefone;
	
	public Telefone(String telefone) {
		this.telefone = telefone;
	}
	public Telefone() {
		// TODO Auto-generated constructor stub
	}
	public String getTelefone() {
		return telefone;
	}
	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

}
