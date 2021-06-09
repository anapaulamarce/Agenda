package br.com.agenda.controller;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ValidationException;

import br.com.agenda.dao.UsuarioDAO;
import br.com.agenda.model.Usuario;

@Named
@ViewScoped
public class CadastroController implements Serializable {

	private static final long serialVersionUID = -7450156267371496819L;
	
	@Inject
	private UsuarioDAO dao;
	@Inject
	private Usuario usuario;
	
	public void cadastrar() {
		try {
			if(usuario.getLogin().isEmpty())
				throw new ValidationException("Preencha o seu login");
			if(usuario.getSenha().isEmpty())
				throw new ValidationException("Preencha a sua senha");
			if(usuario.getNome().isEmpty())
				throw new ValidationException("Preencha o seu nome");
			if(usuario.getEmail().isEmpty())
				throw new ValidationException("Preencha o seu email");
			
			if(!dao.usuarioExistente(usuario.getLogin())) {
				dao.cadastrarUsuario(usuario);
				FacesMessage msg = new FacesMessage("Cadastro realizado com sucesso", String.valueOf(usuario.getNome()));
		        FacesContext.getCurrentInstance().addMessage(null, msg);
		        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
			}
			FacesContext.getCurrentInstance().getExternalContext().redirect("login.xhtml");
		} catch (ValidationException ve) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));	
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
		}
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

}
