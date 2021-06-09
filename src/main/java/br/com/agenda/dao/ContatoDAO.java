package br.com.agenda.dao;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.sql.DataSource;
import br.com.agenda.model.Telefone;
import br.com.agenda.model.Contato;
import br.com.agenda.model.Endereco;

public class ContatoDAO implements Serializable {

	private static final long serialVersionUID = -7463891788753337893L;

	@Resource(mappedName = "java:/agenda")
	private DataSource dataSource;
	
	@Inject
	private TelefoneDAO telefoneDao;
	@Inject
	private EnderecoDAO enderecoDao;


	public void salvarContato(Contato contato) throws Exception {
		String sqlCmdContato = "INSERT INTO CONTATO(NOME) VALUES (?)";
		String sqlCmdTelefones = "INSERT INTO TELEFONE(ID, TELEFONE) VALUES (?, ?)";
		String sqlCmdEnderecos = "INSERT INTO ENDERECO(ID, BAIRRO, CEP, CIDADE, COMPLEMENTO, LOGRADOURO, UF) VALUES (?,?,?,?,?,?,?)";
		int idContato = 0;
		String[] idContatoKey = {"ID"};
		try (Connection conn = dataSource.getConnection();
				PreparedStatement stmtContato = conn.prepareStatement(sqlCmdContato, idContatoKey);
				PreparedStatement stmtTelefones = conn.prepareStatement(sqlCmdTelefones);
				PreparedStatement stmtEnderecos = conn.prepareStatement(sqlCmdEnderecos);) {
					conn.setAutoCommit(false);
					//Salvar Contato
					stmtContato.setString(1, contato.getNome());
					stmtContato.executeUpdate();
	
					try (ResultSet generatedKeys = stmtContato.getGeneratedKeys()) {
						if (generatedKeys.next()) {
							idContato = generatedKeys.getInt(1);
						}
					}
					//Salvar Telefones
					for(int i = 0; i < contato.getTelefones().size(); i++) {
						stmtTelefones.setInt(1, idContato);
						stmtTelefones.setString(2, contato.getTelefones().get(i).getTelefone());
						stmtTelefones.executeUpdate();
					}
					
					//Salvar Enderecos
					for(int i = 0; i < contato.getEnderecos().size(); i++) {
						int cont = 1;
						stmtEnderecos.setInt(cont++, idContato);
						stmtEnderecos.setString(cont++, contato.getEnderecos().get(i).getBairro());
						stmtEnderecos.setString(cont++, contato.getEnderecos().get(i).getCep());
						stmtEnderecos.setString(cont++, contato.getEnderecos().get(i).getCidade());
						if(contato.getEnderecos().get(i).getComplemento() != null)
							stmtEnderecos.setString(cont++, contato.getEnderecos().get(i).getComplemento());
						else
							stmtEnderecos.setNull(cont++, Types.NULL);
						stmtEnderecos.setString(cont++, contato.getEnderecos().get(i).getLogradouro());
						stmtEnderecos.setString(cont++, contato.getEnderecos().get(i).getUf());
						stmtEnderecos.executeUpdate();
					}	
					conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Erro no banco");
		}
		
	}
	
	public List<Contato> recuperarContatos() throws Exception {
		String sqlCmd = "SELECT * FROM CONTATO";
		String sqlCmdTelefone = "SELECT * FROM TELEFONE WHERE ID = ?";
		String sqlCmdEndereco = "SELECT * FROM ENDERECO WHERE ID = ?";
		List<Contato> listaContatos = new ArrayList<>();
		List<Telefone> listaTelefones = new ArrayList<>();
		List<Endereco> listaEnderecos = new ArrayList<>();
		String[] idContatoKey = {"ID"};
		try (Connection conn = dataSource.getConnection(); 
				PreparedStatement stmt = conn.prepareStatement(sqlCmd, idContatoKey);
				PreparedStatement stmtTelefone = conn.prepareStatement(sqlCmdTelefone);
				PreparedStatement stmtEndereco = conn.prepareStatement(sqlCmdEndereco);) {
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					Contato contato = new Contato();
					contato.setId(rs.getInt("ID"));
					contato.setNome(rs.getString("NOME"));
					stmtTelefone.setInt(1, rs.getInt("ID"));
					stmtEndereco.setInt(1, rs.getInt("ID"));
					
					try(ResultSet rsTelefones = stmtTelefone.executeQuery()) {
						while (rsTelefones.next()) {
							Telefone telefone = new Telefone();
							telefone.setTelefone(rsTelefones.getString("TELEFONE"));
							listaTelefones.add(telefone);
						}
					}
					
					try(ResultSet rsEnderecos = stmtEndereco.executeQuery()) {
						while (rsEnderecos.next()) {
							Endereco endereco = new Endereco();
							endereco.setBairro(rsEnderecos.getString("BAIRRO"));
							endereco.setCep(rsEnderecos.getString("CEP"));
							endereco.setCidade(rsEnderecos.getString("CIDADE"));
							endereco.setComplemento(rsEnderecos.getString("COMPLEMENTO"));
							endereco.setLogradouro(rsEnderecos.getString("LOGRADOURO"));
							endereco.setUf(rsEnderecos.getString("UF"));
							listaEnderecos.add(endereco);
						}
					}
					
					contato.setEndereco(listaEnderecos);
					contato.setTelefones(listaTelefones);
					listaContatos.add(contato);
				}
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			throw new Exception("Erro no banco");
		}
		return listaContatos;
	}
	
	public Contato recuperarContato(int id) throws Exception {
		String sqlCmd = "SELECT * FROM CONTATO WHERE ID = ?";
		String sqlCmdTelefone = "SELECT * FROM TELEFONE WHERE ID = ?";
		String sqlCmdEndereco = "SELECT * FROM ENDERECO WHERE ID = ?";
		List<Telefone> listaTelefones = new ArrayList<>();
		List<Endereco> listaEnderecos = new ArrayList<>();
		Contato contato = new Contato();
		
		try (Connection conn = dataSource.getConnection(); 
				PreparedStatement stmt = conn.prepareStatement(sqlCmd);
				PreparedStatement stmtTelefone = conn.prepareStatement(sqlCmdTelefone);
				PreparedStatement stmtEndereco = conn.prepareStatement(sqlCmdEndereco);) {
					stmt.setInt(1, id);
					try (ResultSet rs = stmt.executeQuery()) {
						while (rs.next()) {
							contato.setId(id);
							contato.setNome(rs.getString("NOME"));
							stmtTelefone.setInt(1, id);
							stmtEndereco.setInt(1, id);
							
							try(ResultSet rsTelefones = stmtTelefone.executeQuery()) {
								while (rsTelefones.next()) {
									Telefone telefone = new Telefone();
									telefone.setTelefone(rsTelefones.getString("TELEFONE"));
									listaTelefones.add(telefone);
								}
							}
							
							try(ResultSet rsEnderecos = stmtEndereco.executeQuery()) {
								while (rsEnderecos.next()) {
									Endereco endereco = new Endereco();
									endereco.setBairro(rsEnderecos.getString("BAIRRO"));
									endereco.setCep(rsEnderecos.getString("CEP"));
									endereco.setCidade(rsEnderecos.getString("CIDADE"));
									endereco.setComplemento(rsEnderecos.getString("COMPLEMENTO"));
									endereco.setLogradouro(rsEnderecos.getString("LOGRADOURO"));
									endereco.setUf(rsEnderecos.getString("UF"));
									listaEnderecos.add(endereco);
								}
							}
							
							contato.setEndereco(listaEnderecos);
							contato.setTelefones(listaTelefones);
						}
			}
		} catch(Exception e) {
			e.printStackTrace();
			throw new Exception("Erro no banco");
		}
		return contato;
	}
	
	public void atualizarContato(Contato contato) throws Exception {
		String sqlCmdContato = "UPDATE CONTATO SET NOME = ? WHERE ID = ?";
		
		String sqlCmdRemoverTelefone = "DELETE FROM TELEFONE WHERE ID = ?";
		String sqlCmdAdicionarTelefone = "INSERT INTO TELEFONE(ID, TELEFONE) VALUES(?, ?)";
		String sqlCmdEditarTelefone = "UPDATE TELEFONE SET TELEFONE = ? WHERE ID = ?";
		
		String sqlCmdRemoverEndereco = "DELETE FROM ENDERECO WHERE ID = ?";
		String sqlCmdAdicionarEndereco = "INSERT INTO ENDERECO(ID, BAIRRO, CEP, CIDADE, COMPLEMENTO, LOGRADOURO, UF) VALUES (?,?,?,?,?,?,?)";
		String sqlCmdEditarEndereco = "UPDATE ENDERECO SET BAIRRO = ?, CEP = ?, CIDADE = ?, COMPLEMENTO = ?, LOGRADOURO = ?, UF = ? WHERE ID = ?";
		
		try (Connection conn = dataSource.getConnection(); 
				PreparedStatement stmt = conn.prepareStatement(sqlCmdContato);
				PreparedStatement stmtRemoverTelefone = conn.prepareStatement(sqlCmdRemoverTelefone);
				PreparedStatement stmtAdicionarTelefone = conn.prepareStatement(sqlCmdAdicionarTelefone);
				PreparedStatement stmtEditarTelefone = conn.prepareStatement(sqlCmdEditarTelefone);
				PreparedStatement stmtRemoverEndereco = conn.prepareStatement(sqlCmdRemoverEndereco);
				PreparedStatement stmtAdicionarEndereco = conn.prepareStatement(sqlCmdAdicionarEndereco);
				PreparedStatement stmtEditarEndereco = conn.prepareStatement(sqlCmdEditarEndereco);) {
					conn.setAutoCommit(false);
					stmt.setString(1, contato.getNome());
					stmt.setInt(2, contato.getId());
					stmt.executeUpdate();
				

					List<Telefone> telefonesOld = telefoneDao.recuperarTelefones(contato.getId());
					List<Telefone> telefonesNew = contato.getTelefones();
					
					List<Endereco> enderecosOld = enderecoDao.recuperarEnderecos(contato.getId());
					List<Endereco> enderecosNew = contato.getEnderecos();
					
					List<Telefone> removidosTelefone = new ArrayList<>(telefonesOld);
					List<Telefone> adicionadosTelefone = new ArrayList<>(telefonesNew);
					List<Telefone> alteradosTelefone = new ArrayList<>(telefonesNew);
					
					List<Endereco> removidosEndereco = new ArrayList<>(enderecosOld);
					List<Endereco> adicionadosEndereco = new ArrayList<>(enderecosNew);
					List<Endereco> alteradosEndereco = new ArrayList<>(enderecosNew);

					removidosTelefone.removeAll(telefonesNew);
					adicionadosTelefone.removeAll(telefonesOld);
					alteradosTelefone.removeAll(adicionadosTelefone);
					alteradosTelefone.removeAll(removidosTelefone);

					
					removidosEndereco.removeAll(enderecosNew);
					adicionadosEndereco.removeAll(enderecosOld);
					alteradosEndereco.removeAll(adicionadosEndereco);
					alteradosEndereco.removeAll(removidosEndereco);
					
					//Excluir telefones removidos da lista
					for(int i = 0; i < removidosTelefone.size(); i++) {
						stmtRemoverTelefone.setInt(1, contato.getId());
						stmtRemoverTelefone.executeUpdate();
					}
					
					//Adicionar telefones inseridos na lista
					for(int i = 0; i < adicionadosTelefone.size(); i++) {
						stmtAdicionarTelefone.setInt(1, contato.getId());
						stmtAdicionarTelefone.setString(2, adicionadosTelefone.get(i).getTelefone());
						stmtAdicionarTelefone.executeUpdate();
					}
					
					//Editar telefones que foram alterados na lista
					for(int i = 0; i < alteradosTelefone.size(); i++) {
						stmtEditarTelefone.setString(1, alteradosTelefone.get(i).getTelefone());
						stmtEditarTelefone.setInt(2, contato.getId());
						stmtEditarTelefone.executeUpdate();
					}
					
					//Excluir enderecos removidos da lista
					for(int i = 0; i < removidosEndereco.size(); i++) {
						stmtRemoverEndereco.setInt(1, contato.getId());
						stmtRemoverEndereco.executeUpdate();
					}
					
					//Adicionar enderecos inseridos na lista
					for(int i = 0; i < adicionadosEndereco.size(); i++) {
						int cont = 1;
						stmtAdicionarEndereco.setInt(cont++, contato.getId());
						stmtAdicionarEndereco.setString(cont++, adicionadosEndereco.get(i).getBairro());
						stmtAdicionarEndereco.setString(cont++, adicionadosEndereco.get(i).getCep());
						stmtAdicionarEndereco.setString(cont++, adicionadosEndereco.get(i).getCidade());
						if(adicionadosEndereco.get(i).getComplemento() != null)
							stmtAdicionarEndereco.setString(cont++, adicionadosEndereco.get(i).getComplemento());
						else
							stmtAdicionarEndereco.setNull(cont++, Types.NULL);
						stmtAdicionarEndereco.setString(cont++, adicionadosEndereco.get(i).getLogradouro());
						stmtAdicionarEndereco.setString(cont++, adicionadosEndereco.get(i).getUf());
						stmtAdicionarEndereco.executeUpdate();
					}
					
					//Editar enderecos que foram alterados na lista
					for(int i = 0; i < alteradosEndereco.size(); i++) {
						int cont = 1;
						stmtEditarEndereco.setString(cont++, alteradosEndereco.get(i).getBairro());
						stmtEditarEndereco.setString(cont++, alteradosEndereco.get(i).getCep());
						stmtEditarEndereco.setString(cont++, alteradosEndereco.get(i).getCidade());
						if(alteradosEndereco.get(i).getComplemento() != null)
							stmtEditarEndereco.setString(cont, alteradosEndereco.get(i).getComplemento());
						else
							stmtEditarEndereco.setNull(cont++, Types.NULL);
						stmtEditarEndereco.setString(cont++, alteradosEndereco.get(i).getLogradouro());
						stmtEditarEndereco.setString(cont++, alteradosEndereco.get(i).getUf());
						stmtEditarEndereco.executeUpdate();
					}
					conn.commit();	

		} catch(Exception e) {
			e.printStackTrace();
			throw new Exception("Erro no banco");
		}
	}
	
	public boolean contatoExistente(Contato contato) throws Exception {
		String sqlCmd = "SELECT 1 FROM CONTATO WHERE ID = ?";
		try (Connection conn = dataSource.getConnection(); 
				PreparedStatement stmt = conn.prepareStatement(sqlCmd);) {
					stmt.setInt(1, contato.getId());
					try (ResultSet rs = stmt.executeQuery()) {
						if(rs.next())
							return true;
					}
		}
		return false;
	}

	public void removerContato(Integer id) throws Exception {
		String sqlCmdContato = "DELETE FROM CONTATO WHERE ID = ?";
		try (Connection conn = dataSource.getConnection(); 
				PreparedStatement stmt = conn.prepareStatement(sqlCmdContato);) {
					stmt.setInt(1, id);
					stmt.executeUpdate();
		} catch(Exception e) {
			e.printStackTrace();
			throw new Exception("Erro no banco");
		}
	}
		
	
}