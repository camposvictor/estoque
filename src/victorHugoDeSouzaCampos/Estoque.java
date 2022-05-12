package victorHugoDeSouzaCampos;

import java.util.ArrayList;
import java.util.Date;

import victorHugoDeSouzaCampos.data.RepositorioProduto;
import victorHugoDeSouzaCampos.exceptions.*;

public class Estoque implements InterfaceEstoque {
  private RepositorioProduto repo;

  public Estoque() {
    this.repo = new RepositorioProduto();
  }

  private int verificarNaoNegativo(int valor) throws DadosInvalidos {
    if (valor < 0)
      throw new DadosInvalidos();

    return valor;
  }

  private double verificarNaoNegativo(double valor) throws DadosInvalidos {
    if (valor < 0)
      throw new DadosInvalidos();

    return valor;
  }

  private String verificarNaoVazio(String valor) throws DadosInvalidos {
    if (valor == null || valor.isEmpty())
      throw new DadosInvalidos();

    return valor;
  }

  public void incluir(Produto p) throws ProdutoJaCadastrado, DadosInvalidos {
    try {
      pesquisar(p.getCodigo());
      throw new ProdutoJaCadastrado();

    } catch (ProdutoInexistente e) {
    }

    verificarNaoNegativo(p.getCodigo());
    verificarNaoVazio(p.getDescricao());
    verificarNaoNegativo(p.getEstoqueMinimo());
    verificarNaoNegativo(p.getLucro());

    repo.criar(p);
  }

  public Produto pesquisar(int codigo) throws ProdutoInexistente {
    return repo.encontrar(codigo);
  }

  public void comprar(int cod, int quant, double preco, Date validade)
      throws ProdutoInexistente, DadosInvalidos, ProdutoNaoPerecivel {
    final Produto produto = this.pesquisar(cod);

    if (produto instanceof ProdutoPerecivel) {
      ((ProdutoPerecivel) produto).compra(quant, preco, validade);
      repo.atualizarProduto(produto);

      return;
    }
    if (validade != null)
      throw new ProdutoNaoPerecivel();

    produto.compra(quant, preco);
    repo.atualizarProduto(produto);
  }

  public double vender(int codigo, int quant) throws ProdutoInexistente, ProdutoVencido, DadosInvalidos {
    final Produto produto = this.pesquisar(codigo);

    double precoDeVenda = produto.venda(quant);
    repo.atualizarProduto(produto);
    return precoDeVenda;
  }

  public ArrayList<Produto> estoqueAbaixoDoMinimo() {
    return repo.encontrarProdutosComEstoqueAbaixoDoMinimo();
  }

  public ArrayList<Produto> estoqueVencido() {
    return repo.encontrarEstoqueVencido();
  }

  public void removerTudo() {
    repo.removerTudo();
  }

  public int quantidadeVencidos(int cod) throws ProdutoInexistente {
    return repo.encontrarQuantidadeVencidos(cod);
  }

}
