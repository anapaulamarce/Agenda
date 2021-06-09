package br.com.agenda.controller;

import java.io.IOException;
import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ValidationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.primefaces.json.JSONObject;
import br.com.agenda.dao.ContatoDAO;
import br.com.agenda.model.Contato;
import br.com.agenda.model.Endereco;
import br.com.agenda.model.Telefone;

@Named
@ViewScoped
public class ContatoController implements Serializable {

	private static final long serialVersionUID = -8364427017022383031L;
	
	private List<Contato> contatos = new ArrayList<>();
	private String telefone;
	@Inject private Endereco endereco;
	@Inject private ContatoDAO dao;
	@Inject private Contato contato;
	private boolean houveAlteracao = false;
	private String nomeInicial;
	
	@PostConstruct
	public void init() {
		try {
			String nomePagina = FacesContext.getCurrentInstance().getViewRoot().getViewId();
			if (nomePagina.contains("/listaContatos.xhtml")) {
				contatos = dao.recuperarContatos();
			} else if(nomePagina.contains("/paginaContatos.xhtml")){
				Map<String, String> parameterMap = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
				String idContato = parameterMap.get("idContato");
				if(idContato.isEmpty()) { // Pagina nova
					endereco = new Endereco();
				} else { // Pagina de edicao
					contato = dao.recuperarContato(Integer.parseInt(idContato));
					nomeInicial = contato.getNome();
				}
			} else {
				
			}
			
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
		}
	}
	
	public void adicionarTelefone() {
		if(telefone == null || telefone.isEmpty())
			 FacesContext.getCurrentInstance().addMessage(null, 
					 new FacesMessage(FacesMessage.SEVERITY_ERROR, "Telefone Inválido", "Telefone Inválido"));
		
		Telefone tel = new Telefone(telefone);
		for(int i = 0; i < contato.getTelefones().size(); i++) {	
			if(contato.getTelefones().get(i).getTelefone().equals(telefone)) {
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Telefone já foi adicionado", String.valueOf(tel.getTelefone()));
		        FacesContext.getCurrentInstance().addMessage(null, msg);
		        return;
			}
		}
		contato.getTelefones().add(tel);
		houveAlteracao = true;
		FacesMessage msg = new FacesMessage("Telefone adicionado", String.valueOf(tel.getTelefone()));
        FacesContext.getCurrentInstance().addMessage(null, msg);
	}
	
	public void adicionarEndereco() {
		try {
			if(endereco.getBairro() == null || endereco.getBairro().isEmpty())
				throw new ValidationException("Bairro Inválido");
			if(endereco.getCidade() == null ||endereco.getCidade().isEmpty())
				throw new ValidationException("Cidade Inválida");
			if(endereco.getLogradouro() == null || endereco.getLogradouro().isEmpty())
				throw new ValidationException("Logradouro Inválido");
			if(endereco.getUf() == null || endereco.getUf().isEmpty())
				throw new ValidationException("UF Inválida");
			if(endereco.getCep() == null || endereco.getCep().isEmpty())
				throw new ValidationException("CEP Inválido");
			
			contato.getEnderecos().add(new Endereco(endereco.getBairro(), endereco.getCidade(), endereco.getCep(), endereco.getComplemento(), endereco.getLogradouro(), endereco.getUf()));
			houveAlteracao = true;
			FacesMessage msg = new FacesMessage("Endereço adicionado", String.valueOf(endereco.getCep()));
	        FacesContext.getCurrentInstance().addMessage(null, msg);
		} catch (ValidationException ve) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));	
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));	
		}
	}
	
	public void carregaCep() throws ParseException, IOException {
		if(!NumberUtils.isParsable(endereco.getCep()) || StringUtils.length(endereco.getCep()) != 8) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "CEP Inválido", String.valueOf(endereco.getCep()));
	        FacesContext.getCurrentInstance().addMessage(null, msg);
	        return;
		}
		
        HttpGet get = new HttpGet("https://viacep.com.br/ws/" + endereco.getCep() + "/json/");

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
	         CloseableHttpResponse response = httpClient.execute(get)) {
	    	 String result = EntityUtils.toString(response.getEntity());
	 		 JSONObject respostaJson = new JSONObject(result);
	 		 if(respostaJson.has("erro")) {
	 			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "CEP Inválido", String.valueOf(endereco.getCep()));
	 	        FacesContext.getCurrentInstance().addMessage(null, msg);
	 		 } else {
	 			if(!respostaJson.getString("bairro").isEmpty()) 
	 				endereco.setBairro(respostaJson.getString("bairro"));
	 			if(!respostaJson.getString("logradouro").isEmpty()) 
	 				endereco.setLogradouro(respostaJson.getString("logradouro"));
	 			if(!respostaJson.getString("complemento").isEmpty()) 
	 				endereco.setComplemento(respostaJson.getString("complemento"));
	 			if(!respostaJson.getString("localidade").isEmpty()) 
	 				endereco.setCidade(respostaJson.getString("localidade"));
	 			if(!respostaJson.getString("uf").isEmpty()) 
	 				endereco.setUf(respostaJson.getString("uf"));
	 		 }
        }

	}
	
	public void salvar() {
		try {
			if(contato.getId() != null && dao.contatoExistente(contato)) {
				if(houveAlteracao())
					dao.atualizarContato(contato);
			} else {
				dao.salvarContato(contato);
			}
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Contato cadastrado com sucesso!", null));
			FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
			FacesContext.getCurrentInstance().getExternalContext().redirect("listaContatos.xhtml");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private boolean houveAlteracao() {
		return !nomeInicial.equals(contato.getNome()) || houveAlteracao;
	}

	public void novoContato() {
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect("paginaContatos.xhtml");
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
		}
	}
	
	public void alterarContato(Contato contato) {
		try {
			if (contato.getId() != 0) {
				FacesContext.getCurrentInstance().getExternalContext()
						.redirect("paginaContatos.xhtml?idContato=" + contato.getId());
			} else {
				throw new InvalidParameterException("ID do Contato não informado");
			}
			
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage("messages",
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
		}
	}
	
	public void removerContato(Contato contato) {
		try {
			if (contato.getId() != 0) {
				dao.removerContato(contato.getId());
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, "Contato removido com sucesso!", null));
				FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
				FacesContext.getCurrentInstance().getExternalContext().redirect("listaContatos.xhtml");
			} else {
				throw new InvalidParameterException("ID do Contato inválido");
			}
			
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage("messages",
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
		}
	}
	
	

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}
	public Contato getContato() {
		return contato;
	}
	
	public void removerTelefone(Telefone telefone) {
		houveAlteracao = true;
		contato.getTelefones().remove(telefone);
	}
	
	public void removerEndereco(Endereco endereco) {
		houveAlteracao = true;
		contato.getEnderecos().remove(endereco);
	}
	
	public void setContato(Contato contato) {
		this.contato = contato;
	}
	public Endereco getEndereco() {
		return endereco;
	}

	public void setEndereco(Endereco endereco) {
		this.endereco = endereco;
	}

	public List<Contato> getContatos() {
		return contatos;
	}

	public void setContatos(List<Contato> contatos) {
		this.contatos = contatos;
	}
	
	public void setHouveAlteracao(boolean houveAlteracao) {
		this.houveAlteracao = houveAlteracao;
	}

}
