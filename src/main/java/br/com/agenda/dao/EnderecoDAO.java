package br.com.agenda.dao;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.sql.DataSource;

import br.com.agenda.model.Endereco;

public class EnderecoDAO implements Serializable {
	
	private static final long serialVersionUID = -2177178659500280133L;
	
	@Resource(mappedName = "java:/agenda")
	private DataSource dataSource;

	
	public List<Endereco> recuperarEnderecos(int id) throws Exception {
		String sqlCmd = "SELECT * FROM ENDERECO WHERE ID = ?";
		List<Endereco> listaEnderecos = new ArrayList<>();
		try (Connection conn = dataSource.getConnection(); 
				PreparedStatement stmt = conn.prepareStatement(sqlCmd);) {
					stmt.setInt(1, id);
					try (ResultSet rs = stmt.executeQuery()) {
						while(rs.next()) {
							Endereco endereco = new Endereco();
							endereco.setBairro(rs.getString("BAIRRO"));
							endereco.setCep(rs.getString("CEP"));
							endereco.setCidade(rs.getString("CIDADE"));
							endereco.setComplemento(rs.getString("COMPLEMENTO"));
							endereco.setLogradouro(rs.getString("LOGRADOURO"));
							endereco.setUf(rs.getString("UF"));
							listaEnderecos.add(endereco);
						}
					}
		} catch (Exception e ) {
			e.printStackTrace();
			throw new Exception("Erro no banco");
		}
		return listaEnderecos;
	}
}
