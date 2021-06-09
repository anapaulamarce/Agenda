package br.com.agenda.model;

import java.io.Serializable;

public class Endereco implements Serializable {

	private static final long serialVersionUID = 8657756547243724690L;
	private String bairro;
	private String cidade;
	private String cep;
	private String complemento;
	private String logradouro;
	private String uf;
	
	public Endereco(String bairro, String cidade, String cep, String complemento, String logradouro, String uf) {
		this.bairro = bairro;
		this.cidade = cidade;
		this.cep = cep;
		this.complemento = complemento;
		this.logradouro = logradouro;
		this.uf = uf;
	}
	
	public Endereco() {
		// TODO Auto-generated constructor stub
	}

	public String getBairro() {
		return bairro;
	}
	public void setBairro(String bairro) {
		this.bairro = bairro;
	}
	public String getCidade() {
		return cidade;
	}
	public void setCidade(String cidade) {
		this.cidade = cidade;
	}
	public String getCep() {
		return cep;
	}
	public void setCep(String cep) {
		this.cep = cep;
	}
	public String getComplemento() {
		return complemento;
	}
	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}
	public String getLogradouro() {
		return logradouro;
	}
	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}
	public String getUf() {
		return uf;
	}
	public void setUf(String uf) {
		this.uf = uf;
	}

}
