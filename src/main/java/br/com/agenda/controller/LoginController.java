package br.com.agenda.controller;

import java.io.IOException;
import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ValidationException;
import br.com.agenda.dao.UsuarioDAO;
import br.com.agenda.model.Usuario;

@Named
@SessionScoped
public class LoginController implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8354875066409112030L;
	@Inject
	private Usuario usuario;
	@Inject
	private UsuarioDAO dao;
	
	private String login;
	private String senha;
	
	public void logar() {
		try {
			if (login == null)
				throw new ValidationException("Preencha o seu login");
			if (senha == null)
				throw new ValidationException("Preencha sua senha");
			
			usuario = dao.buscarLogin(login, senha);
			FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("usuario", usuario);
			FacesContext.getCurrentInstance().getExternalContext().redirect("listaContatos.xhtml");
		} catch (ValidationException ve) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));	
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, 
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
		}
	}
	
	public void cadastrar() throws Exception {
		FacesContext.getCurrentInstance().getExternalContext().redirect("cadastrar.xhtml");
	}
	
	public void logout() throws Exception {
		try {
			FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("usuario");
			FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
			FacesContext.getCurrentInstance().getExternalContext().redirect("/agenda/login.xhtml");
		} catch (IOException e) {
			FacesContext.getCurrentInstance().addMessage("messages",
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
		}
	}
	
	public String getLogin() {
		return login;
	}
	
	public void setLogin(String login) {
		this.login = login;
	}
	
	public String getSenha() {
		return senha;
	}
	
	public void setSenha(String senha) {
		this.senha = senha;
	}
	
	public Usuario getUsuario() {
		return usuario;
	}

}
