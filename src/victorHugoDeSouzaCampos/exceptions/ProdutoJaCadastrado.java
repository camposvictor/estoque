package victorHugoDeSouzaCampos.exceptions;

public class ProdutoJaCadastrado extends Exception {
  public ProdutoJaCadastrado() {
    super("Produto já cadastrado");
  }
}
