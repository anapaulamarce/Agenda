package br.com.agenda.dao;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.annotation.Resource;
import javax.sql.DataSource;
import javax.validation.ValidationException;
import br.com.agenda.model.Usuario;

public class UsuarioDAO implements Serializable {

	private static final long serialVersionUID = -7463891788753337893L;

	@Resource(mappedName = "java:/agenda")
	private DataSource dataSource;

	public Usuario buscarLogin(String login, String senha) throws Exception {
		String sqlCmd = "SELECT * FROM USUARIO WHERE LOGIN = ? AND SENHA = ?"; 
		
		try (Connection conn = dataSource.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sqlCmd)) {
			
			stmt.setString(1, login);
			stmt.setString(2, senha);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					Usuario usuario = new Usuario();
					usuario.setLogin(rs.getString("LOGIN"));
					usuario.setNome(rs.getString("NOME"));
					usuario.setEmail(rs.getString("EMAIL"));
					return usuario;
				} else {
					throw new ValidationException("Usuário não encontrado!");
				}
			}
		} catch (ValidationException ve) {
			throw ve;
		} catch (Exception e) {
			throw new Exception("Erro no BD");
		}
	}
	
	public boolean usuarioExistente(String login) throws Exception {
		String sqlCmd = "SELECT 1 FROM USUARIO WHERE LOGIN = ?";
		
		try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sqlCmd)) {
			stmt.setString(1, login);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					throw new ValidationException("Usuário já cadastrado!");
				} else {
					return false;
				}
			}
					
		} catch (ValidationException ve) {
			throw ve;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Erro no BD");
		}
	}
	
	public void cadastrarUsuario(Usuario usuario) throws Exception {
		String sqlCmd = "INSERT INTO USUARIO(LOGIN, NOME, SENHA, EMAIL) VALUES(?,?,?,?)";
		
		try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sqlCmd)) {
			stmt.setString(1, usuario.getLogin());
			stmt.setString(2, usuario.getNome());
			stmt.setString(3, usuario.getSenha());
			stmt.setString(4, usuario.getEmail());
			stmt.executeUpdate();
					
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Erro no BD");
		}
	}
	
}