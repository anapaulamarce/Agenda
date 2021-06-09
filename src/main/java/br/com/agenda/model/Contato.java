package br.com.agenda.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Contato implements Serializable {

	private static final long serialVersionUID = -1249859674563911059L;
	private Integer id;
	private String nome;
	private List<Telefone> telefones = new ArrayList<>();
	private List<Endereco> enderecos = new ArrayList<>();
	
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public  List<Endereco> getEnderecos() {
		return enderecos;
	}
	public void setEndereco( List<Endereco> enderecos) {
		this.enderecos = enderecos;
	}
	public List<Telefone> getTelefones() {
		return telefones;
	}
	public void setTelefones(List<Telefone> telefones) {
		this.telefones = telefones;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	

}
