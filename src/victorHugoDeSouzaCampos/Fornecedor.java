package victorHugoDeSouzaCampos;

public class Fornecedor {
  private int cnpj;
  private String nome;

  public Fornecedor(int cnpj, String nome) {
    this.cnpj = cnpj;
    this.nome = nome;
  }

  public int getCnpj() {
    return cnpj;
  }

  public void setCnpj(int cnpj) {
    this.cnpj = cnpj;
  }

  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

}