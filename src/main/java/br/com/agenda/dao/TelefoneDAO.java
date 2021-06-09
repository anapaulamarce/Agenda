package br.com.agenda.dao;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.sql.DataSource;
import br.com.agenda.model.Telefone;

public class TelefoneDAO implements Serializable {
	
	private static final long serialVersionUID = -2177178659500280133L;
	
	@Resource(mappedName = "java:/agenda")
	private DataSource dataSource;

	
	public List<Telefone> recuperarTelefones(int id) throws Exception {
		String sqlCmd = "SELECT * FROM TELEFONE WHERE ID = ?";
		List<Telefone> listaTelefones = new ArrayList<>();
		try (Connection conn = dataSource.getConnection(); 
				PreparedStatement stmt = conn.prepareStatement(sqlCmd);) {
					stmt.setInt(1, id);
					try (ResultSet rs = stmt.executeQuery()) {
						while(rs.next()) {
							Telefone telefone = new Telefone();
							telefone.setTelefone(rs.getString("TELEFONE"));
							listaTelefones.add(telefone);
						}
					}
		} catch (Exception e ) {
			e.printStackTrace();
			throw new Exception("Erro no banco");
		}
		return listaTelefones;
	}
}
