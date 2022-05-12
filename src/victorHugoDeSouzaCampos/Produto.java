package victorHugoDeSouzaCampos;

import victorHugoDeSouzaCampos.exceptions.*;

import java.util.Date;

public class Produto {
  protected int codigo;
  protected String descricao;
  protected double precoDeCompra;
  protected double precoDeVenda;

  protected int quantidade;
  protected int estoqueMinimo;
  protected double lucro;

  public Produto() {
  }

  public Produto(int cod, double lucro, double precoDeCompra, double precoDeVenda, int quant, String desc, int min,
      Fornecedor forn) throws DadosInvalidos {
    this.codigo = cod;
    this.descricao = desc;
    this.estoqueMinimo = min;
    this.quantidade = quant;
    this.precoDeCompra = precoDeCompra;
    this.precoDeVenda = precoDeVenda;
    this.lucro = lucro;
  }

  public Produto(int cod, String desc, int min, double lucro, Fornecedor forn) throws DadosInvalidos {
    this.codigo = cod;
    this.descricao = desc;
    this.estoqueMinimo = min;
    this.quantidade = 0;
    this.precoDeCompra = 0;
    this.precoDeVenda = 0;
    this.lucro = lucro;
  }

  protected <T> T verificarNaoNulo(T valor) throws DadosInvalidos {
    if (valor == null)
      throw new DadosInvalidos();

    return valor;
  }

  protected int verificarNaoNegativo(int valor) throws DadosInvalidos {
    if (valor < 0)
      throw new DadosInvalidos();

    return valor;
  }

  protected double verificarNaoNegativo(double valor) throws DadosInvalidos {
    if (valor < 0)
      throw new DadosInvalidos();

    return valor;
  }

  protected String verificarNaoVazio(String valor) throws DadosInvalidos {
    if (valor == null || valor.isEmpty())
      throw new DadosInvalidos();

    return valor;
  }

  void compra(int quant, double val) throws DadosInvalidos {
    verificarNaoNegativo(quant);
    verificarNaoNegativo(val);

    this.precoDeCompra = (this.precoDeCompra * this.quantidade + val * quant) / (this.quantidade + quant);

    this.quantidade += quant;
    this.precoDeVenda = this.precoDeCompra * (this.lucro + 1);
  }

  void compra(int quant, double val, Date validade) throws DadosInvalidos,
      ProdutoNaoPerecivel {
    throw new ProdutoNaoPerecivel();
  }

  double venda(int quant) throws DadosInvalidos, ProdutoVencido {
    verificarNaoNegativo(quant);

    if (quant > this.quantidade) {
      throw new DadosInvalidos();
    }

    this.quantidade -= quant;
    return this.precoDeVenda * quant;
  }

  public int getCodigo() {
    return codigo;
  }

  public void setCodigo(int codigo) {
    this.codigo = codigo;
  }

  public String getDescricao() {
    return descricao;
  }

  public void setDescricao(String descricao) {
    this.descricao = descricao;
  }

  public double getPrecoDeCompra() {
    return precoDeCompra;
  }

  public double getPrecoDeVenda() {
    return precoDeVenda;
  }

  public int getQuantidade() {
    return quantidade;
  }

  public int getEstoqueMinimo() {
    return estoqueMinimo;
  }

  public void setEstoqueMinimo(int estoqueMinimo) {
    this.estoqueMinimo = estoqueMinimo;
  }

  public double getLucro() {
    return lucro;
  }

  public void setLucro(double lucro) {
    this.lucro = lucro;
  }

  public void setPrecoDeCompra(double precoDeCompra) {
    this.precoDeCompra = precoDeCompra;
  }

  public void setPrecoDeVenda(double precoDeVenda) {
    this.precoDeVenda = precoDeVenda;
  }

  public void setQuantidade(int quantidade) {
    this.quantidade = quantidade;
  }

}