package victorHugoDeSouzaCampos.data;

import java.sql.*;

public class Conexao {

  public static Connection getConexao() {
    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
      return DriverManager.getConnection("jdbc:mysql://localhost:6603/estoque", "db_user", "db_pwd");
    } catch (Exception e) {
      return null;
    }
  }
}
