package victorHugoDeSouzaCampos.data;

import victorHugoDeSouzaCampos.*;
import victorHugoDeSouzaCampos.exceptions.ProdutoInexistente;
import victorHugoDeSouzaCampos.exceptions.ProdutoJaCadastrado;

import java.sql.*;
import java.util.ArrayList;

public class RepositorioProduto {
  private Connection conexao;
  private String SQL_INSERIR = "insert into produtos (cod, lucro, preco_compra,preco_venda, quant, estoque_min, descricao, tipo) values (?,?,?,?,?,?,?, ?);";
  private String SQL_SELECT = "SELECT * FROM produtos where cod = ?;";
  private String SQL_ATUALIZAR = "update produtos set quant = ?, preco_compra = ?, preco_venda = ? where cod= ?;";
  private String SQL_INSERIR_LOTE = "insert into lotes (cod_produto, validade, quant) values(?,?,?);";
  private String SQL_SELECT_LOTES = "select * from lotes where cod_produto = ?";
  private String SQL_SELECT_MIN = "select * from produtos where quant < estoque_min;";
  private String SQL_SELECT_ESTOQUE_VENCIDO = "select * from produtos where exists (select cod_produto from lotes where produtos.cod = lotes.cod_produto and lotes.validete < getdate());";
  private String SQL_SELECT_QUANT_VENCIDOS = "select sum(distinct quant) quant from lotes where lotes.cod_produto = ? and lotes.validade < getdate();";
  private String SQL_REMOVER_TUDO = "delete produtos from produtos;";

  public RepositorioProduto() {
    conexao = Conexao.getConexao();
  }

  boolean ePerecivel(Produto produto) {
    return produto instanceof ProdutoPerecivel;
  }

  public void criarLote(Lote lote, int cod) {
    try {
      PreparedStatement inserirLote = conexao.prepareStatement(SQL_INSERIR_LOTE);

      inserirLote.setInt(1, cod);
      inserirLote.setDate(2, new Date(lote.getValidade().getTime()));
      inserirLote.setInt(3, lote.getQuant());

      inserirLote.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void criarLotes(ArrayList<Lote> lotes, int cod) {
    try {
      PreparedStatement inserirLote = conexao.prepareStatement(SQL_INSERIR_LOTE);

      for (Lote lote : lotes) {
        inserirLote.setInt(1, cod);
        inserirLote.setDate(2, new Date(lote.getValidade().getTime()));
        inserirLote.setInt(3, lote.getQuant());

        inserirLote.executeUpdate();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public ArrayList<Lote> encontrarLotes(int cod) {
    try {
      ArrayList<Lote> lotes = new ArrayList<>();

      PreparedStatement selectLotes = conexao.prepareStatement(SQL_SELECT_LOTES);

      selectLotes.setInt(1, cod);

      ResultSet rs = selectLotes.executeQuery();

      while (rs.next()) {
        Lote lote = new Lote(rs.getInt("quant"), new java.util.Date(rs.getDate("validade").getTime()));
        lotes.add(lote);
      }

      return lotes;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  public void criar(Produto produto) throws ProdutoJaCadastrado {
    try {
      encontrar(produto.getCodigo());
      throw new ProdutoJaCadastrado();
    } catch (ProdutoInexistente e) {
    }

    try {

      PreparedStatement inserirProduto = conexao.prepareStatement(SQL_INSERIR);

      inserirProduto.setInt(1, produto.getCodigo());
      inserirProduto.setFloat(2, (float) produto.getLucro());
      inserirProduto.setFloat(3, (float) produto.getPrecoDeCompra());
      inserirProduto.setFloat(4, (float) produto.getPrecoDeVenda());
      inserirProduto.setInt(5, produto.getQuantidade());
      inserirProduto.setInt(6, produto.getEstoqueMinimo());
      inserirProduto.setString(7, produto.getDescricao());
      inserirProduto.setString(8, ePerecivel(produto) ? "perecivel" : "normal");

      inserirProduto.executeUpdate();

      if (!(produto instanceof ProdutoPerecivel))
        return;

      criarLotes(((ProdutoPerecivel) produto).getLotes(), produto.getCodigo());

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public Produto encontrar(int cod) throws ProdutoInexistente {
    try {
      PreparedStatement selectProduto = conexao.prepareStatement(SQL_SELECT);
      selectProduto.setInt(1, cod);

      ResultSet rs = selectProduto.executeQuery();
      Produto produto = null;

      if (!rs.next())
        throw new ProdutoInexistente();
      if (rs.getString("tipo").compareTo("normal") == 0)
        produto = new Produto();
      else {
        produto = new ProdutoPerecivel();
        ((ProdutoPerecivel) produto).setLotes(encontrarLotes(cod));
      }

      produto.setCodigo(rs.getInt("cod"));
      produto.setLucro(rs.getFloat("lucro"));
      produto.setPrecoDeCompra(rs.getFloat("preco_compra"));
      produto.setPrecoDeVenda(rs.getFloat("preco_venda"));
      produto.setQuantidade(rs.getInt("quant"));
      produto.setEstoqueMinimo(rs.getInt("estoque_min"));
      produto.setDescricao(rs.getString("descricao"));

      return produto;
    } catch (

    SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  public void atualizarProduto(Produto produto) {
    try {
      PreparedStatement atualizarProduto = conexao.prepareStatement(SQL_ATUALIZAR);

      atualizarProduto.setInt(1, produto.getQuantidade());
      atualizarProduto.setDouble(2, produto.getPrecoDeCompra());
      atualizarProduto.setDouble(3, produto.getPrecoDeVenda());
      atualizarProduto.setInt(4, produto.getCodigo());

      atualizarProduto.executeUpdate();

      if (!(produto instanceof ProdutoPerecivel))
        return;

      int tamanho = ((ProdutoPerecivel) produto).getLotes().size();
      criarLote(((ProdutoPerecivel) produto).getLotes().get(tamanho - 1), produto.getCodigo());

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public ArrayList<Produto> encontrarProdutosComEstoqueAbaixoDoMinimo() {
    try {
      ArrayList<Produto> produtos = new ArrayList<>();
      PreparedStatement seleteAbaixoDoMin = conexao.prepareStatement(SQL_SELECT_MIN);

      ResultSet rs = seleteAbaixoDoMin.executeQuery();

      while (rs.next()) {
        Produto produto;
        if (rs.getString("tipo") == "produto")
          produto = new Produto();
        else {
          produto = new ProdutoPerecivel();
          ((ProdutoPerecivel) produto).setLotes(encontrarLotes(rs.getInt("cod")));
        }

        produto.setCodigo(rs.getInt("cod"));
        produto.setLucro(rs.getFloat("lucro"));
        produto.setPrecoDeCompra(rs.getFloat("preco_compra"));
        produto.setPrecoDeVenda(rs.getFloat("preco_venda"));
        produto.setQuantidade(rs.getInt("quant"));
        produto.setEstoqueMinimo(rs.getInt("estoque_min"));
        produto.setDescricao(rs.getString("descricao"));

        produtos.add(produto);
      }
      return produtos;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  public ArrayList<Produto> encontrarEstoqueVencido() {
    try {
      ArrayList<Produto> produtos = new ArrayList<>();

      PreparedStatement selectEstoqueVencido = conexao.prepareStatement(SQL_SELECT_ESTOQUE_VENCIDO);

      ResultSet rs = selectEstoqueVencido.executeQuery();

      while (rs.next()) {
        Produto produto;
        if (rs.getString("tipo") == "produto")
          produto = new Produto();
        else {
          produto = new ProdutoPerecivel();
          ((ProdutoPerecivel) produto).setLotes(encontrarLotes(rs.getInt("cod")));
        }

        produto.setCodigo(rs.getInt("cod"));
        produto.setLucro(rs.getFloat("lucro"));
        produto.setPrecoDeCompra(rs.getFloat("preco_compra"));
        produto.setPrecoDeVenda(rs.getFloat("preco_venda"));
        produto.setQuantidade(rs.getInt("quant"));
        produto.setEstoqueMinimo(rs.getInt("estoque_min"));
        produto.setDescricao(rs.getString("descricao"));

        produtos.add(produto);
      }
      return produtos;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  public int encontrarQuantidadeVencidos(int cod) throws ProdutoInexistente {
    encontrar(cod);
    try {
      PreparedStatement selectQuantVencidos = conexao.prepareStatement(SQL_SELECT_QUANT_VENCIDOS);

      selectQuantVencidos.setInt(1, cod);

      ResultSet rs = selectQuantVencidos.executeQuery();

      rs.next();
      return rs.getInt("quant");

    } catch (SQLException e) {
      e.printStackTrace();
      return 0;
    }
  }

  public void removerTudo() {
    try {
      PreparedStatement removerTudo = conexao.prepareStatement(SQL_REMOVER_TUDO);

      removerTudo.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
