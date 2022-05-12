package victorHugoDeSouzaCampos;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;

import org.junit.Test;

import victorHugoDeSouzaCampos.exceptions.*;

public class TesteControleEstoqueCompleto {
  @Test
  public void testarCadastroProduto() throws ProdutoJaCadastrado,
      DadosInvalidos, ProdutoInexistente {
    Fornecedor f1 = new Fornecedor(1, "Forn1");
    Produto p1 = new Produto(1, "coca-cola", 5, 0.20, f1);

    Estoque est = new Estoque();
    est.removerTudo();
    est.incluir(p1);

    Produto outro = est.pesquisar(1);
    assertEquals(1, outro.getCodigo());
    assertEquals("coca-cola", outro.getDescricao());
    assertEquals(5, outro.getEstoqueMinimo());
    assertEquals(0, outro.getPrecoDeVenda(), 0.001);
    assertEquals(0, outro.getPrecoDeCompra(), 0.001);
    assertEquals(0, outro.getQuantidade(), 0.001);
    assertEquals(0.2, outro.getLucro(), 0.001);

    Produto p2 = new Produto(2, "Sabão Omo", 10, 0.5, f1);
    est.incluir(p2);

    outro = est.pesquisar(2);
    assertEquals(2, outro.getCodigo());
    assertEquals("Sabão Omo", outro.getDescricao());
    assertEquals(10, outro.getEstoqueMinimo());
    assertEquals(0, outro.getPrecoDeCompra(), 0.001);
    assertEquals(0, outro.getPrecoDeVenda(), 0.001);
    assertEquals(0, outro.getQuantidade(), 0.001);
    assertEquals(0.5, outro.getLucro(), 0.001);
  }

  @Test
  public void testarCadastroProdutosInvalidos() throws ProdutoJaCadastrado,
      DadosInvalidos {
    Fornecedor f1 = new Fornecedor(1, "Forn1");

    Estoque est = new Estoque();
    est.removerTudo();

    Produto p1 = new Produto(-1, "coca-cola", 5, 0.20, f1);
    try {
      est.incluir(p1);
      fail("Código inválido");
    } catch (DadosInvalidos e) {
      // Pegou dado inválido
    }

    p1 = new Produto(1, null, 5, 0.20, f1);
    try {
      est.incluir(p1);
      fail("Descr inválido");
    } catch (DadosInvalidos e) {
      // Pegou dado inválido
    }

    p1 = new Produto(1, "", 5, 0.20, f1);
    try {
      est.incluir(p1);
      fail("Descr inválido");
    } catch (DadosInvalidos e) {
      // Pegou dado inválido
    }

    p1 = new Produto(1, "Omo", -5, 0.20, f1);
    try {
      est.incluir(p1);
      fail("Min inválido");
    } catch (DadosInvalidos e) {
      // Pegou dado inválido
    }

    p1 = new Produto(1, "Omo", 5, -0.20, f1);
    try {
      est.incluir(p1);
      fail("Lucro inválido");
    } catch (DadosInvalidos e) {
      // Pegou dado inválido
    }

    p1 = new Produto(1, "Omo", 5, 0.20, f1);
    try {
      est.incluir(p1);
      est.incluir(p1);
      fail("Duplicado");
    } catch (ProdutoJaCadastrado e) {
      // Pegou produto cadastrado
    }
  }

  @Test
  public void testarVenda()
      throws ProdutoJaCadastrado, DadosInvalidos, ProdutoInexistente,
      ProdutoNaoPerecivel, ProdutoVencido {
    Fornecedor f1 = new Fornecedor(1, "Forn1");
    Produto p1 = new Produto(1, "coca-cola", 5, 0.5, f1);

    Estoque est = new Estoque();
    est.removerTudo();

    est.incluir(p1);
    est.comprar(1, 10, 2.0, null);
    double val = est.vender(1, 5);

    Produto outro = est.pesquisar(1);
    assertEquals(1, outro.getCodigo());
    assertEquals(5, outro.getQuantidade(), 0.001);
    assertEquals(15, val, 0.001);
  }

  @Test
  public void testarVendaInvalida()
      throws ProdutoJaCadastrado, DadosInvalidos, ProdutoNaoPerecivel,
      ProdutoInexistente, ProdutoVencido {
    Fornecedor f1 = new Fornecedor(1, "Forn1");
    Produto p1 = new Produto(1, "coca-cola", 5, 0.5, f1);

    Estoque est = new Estoque();
    est.removerTudo();
    est.incluir(p1);

    try {
      est.comprar(-1, 10, 2.0, null);
      fail("Codigo inexistente");
    } catch (ProdutoInexistente e) {
    }

    try {
      est.comprar(1, -10, 2.0, null);
      fail("Quant compra invalida");
    } catch (DadosInvalidos e) {
    }

    try {
      est.comprar(1, 10, -2.0, null);
      fail("Preço compra invalida");
    } catch (DadosInvalidos e) {
    }

    est.comprar(1, 10, 2.0, null);
    double val = est.vender(1, 5);

    Produto outro = est.pesquisar(1);
    assertEquals(1, outro.getCodigo());
    assertEquals(5, outro.getQuantidade(), 0.001);
    assertEquals(15, val, 0.001);

    try {
      val = est.vender(-1, 5);
      fail("Codigo inexistente");
    } catch (ProdutoInexistente e) {
    }

    try {
      val = est.vender(1, -5);
      fail("Dados invalidos");
    } catch (DadosInvalidos e) {
    }

    try {
      val = est.vender(1, 500);
      fail("Quantidade maior");
    } catch (DadosInvalidos e) {
    }
  }

  @Test
  public void testarAtualizacaoPrecoDeCompra()
      throws ProdutoJaCadastrado, DadosInvalidos, ProdutoInexistente,
      ProdutoNaoPerecivel {
    Fornecedor f1 = new Fornecedor(1, "Forn1");
    Produto p1 = new Produto(1, "coca-cola", 5, 0.5, f1);

    Estoque est = new Estoque();
    est.removerTudo();

    est.incluir(p1);
    est.comprar(1, 10, 2, null);
    Produto outro = est.pesquisar(1);
    assertEquals(1, outro.getCodigo());
    assertEquals(10, outro.getQuantidade(), 0.001);
    assertEquals(2, outro.getPrecoDeCompra(), 0.001);
    assertEquals(3.0, outro.getPrecoDeVenda(), 0.001);
    est.comprar(1, 10, 4, null);
    assertEquals(4.5, outro.getPrecoDeVenda(), 0.001);
  }

  @Test
  public void testarCompraProdutosPereciveis()
      throws ProdutoJaCadastrado, DadosInvalidos, ProdutoInexistente,
      ProdutoNaoPerecivel {
    Fornecedor f1 = new Fornecedor(1, "Forn1");
    Produto p1 = new Produto(1, "coca-cola", 5, 0.5, f1);
    Produto p2 = new ProdutoPerecivel(2, "Arroz Maria", 10, 0.2, f1);

    Estoque est = new Estoque();
    est.removerTudo();
    Date hoje = new Date();

    Date mes1 = new Date();
    mes1.setMonth(hoje.getMonth() + 1);

    Date mes2 = new Date();
    mes2.setMonth(hoje.getMonth() + 2);

    est.incluir(p1);
    est.incluir(p2);

    est.comprar(1, 10, 2.0, null);
    Produto outro = est.pesquisar(1);
    assertEquals(1, outro.getCodigo());
    assertEquals(10, outro.getQuantidade(), 0.001);
    assertEquals(2, outro.getPrecoDeCompra(), 0.001);
    assertEquals(3.0, outro.getPrecoDeVenda(), 0.001);
    est.comprar(1, 10, 4, null);
    assertEquals(4.5, outro.getPrecoDeVenda(), 0.001);

    est.comprar(2, 10, 2, mes1);
    est.comprar(2, 15, 4, mes2);
    est.comprar(2, 25, 6, mes2);
    Produto maisUm = est.pesquisar(2);
    assertEquals(2, maisUm.getCodigo());
    assertEquals(50, maisUm.getQuantidade(), 0.001);
    assertEquals(4.6, maisUm.getPrecoDeCompra(), 0.001);
    assertEquals(5.52, maisUm.getPrecoDeVenda(), 0.001);

  }

  @Test
  public void testarVendaProdutosPereciveis()
      throws ProdutoJaCadastrado, ProdutoInexistente, ProdutoNaoPerecivel,
      ProdutoVencido, DadosInvalidos {
    Fornecedor f1 = new Fornecedor(1, "Forn1");
    Produto p2 = new ProdutoPerecivel(2, "Arroz Maria", 10, 0.2, f1);

    Estoque est = new Estoque();
    est.removerTudo();
    Date hoje = new Date();
    Date mes1 = new Date();
    mes1.setMonth(hoje.getMonth() + 1);
    Date mes2 = new Date();
    mes2.setMonth(hoje.getMonth() + 2);
    Date mes3 = new Date();
    mes3.setMonth(hoje.getMonth() + 3);

    est.incluir(p2);

    // Compra de 3 lotes de produtos com vencimentos diferentes
    est.comprar(2, 10, 2, mes1);
    est.comprar(2, 15, 4, mes2);
    est.comprar(2, 25, 6, mes3);
    Produto maisUm = est.pesquisar(2);
    assertEquals(50, maisUm.getQuantidade());
    assertEquals(4.6, maisUm.getPrecoDeCompra(), 0.001);
    assertEquals(5.52, maisUm.getPrecoDeVenda(), 0.001);

    // Deveria vender 10 do lote com vencimento no mes1 e 5 do mes2
    double val = est.vender(2, 15);
    assertEquals(35, maisUm.getQuantidade());
    assertEquals(82.8, val, 0.001);

    // Deveria vender 5 do lote com vencimento no mes2 e 15 do mes3
    val = est.vender(2, 20);
    assertEquals(15, maisUm.getQuantidade());
    assertEquals(110.4, val, 0.001);

    // Deveria vender 5 do lote com vencimento no mes2 e 15 do mes3
    val = est.vender(2, 10);
    assertEquals(5, maisUm.getQuantidade());
    assertEquals(55.20, val, 0.001);

    try {
      val = est.vender(2, 20);
      fail("Não deveria vender!");
    } catch (DadosInvalidos e) {
      // Deveria não vender
    }
    assertEquals(5, maisUm.getQuantidade());
  }

  @Test
  public void testarVendaProdutosPereciveisVencidos()
      throws ProdutoJaCadastrado, DadosInvalidos, ProdutoInexistente,
      ProdutoNaoPerecivel, ProdutoVencido {
    Fornecedor f1 = new Fornecedor(1, "Forn1");
    Produto p2 = new ProdutoPerecivel(2, "Arroz Maria", 10, 0.2, f1);
    Produto p3 = new ProdutoPerecivel(3, "Fubá", 10, 0.2, f1);

    Estoque est = new Estoque();
    est.removerTudo();

    Date hoje = new Date();
    Date mes1 = new Date();
    mes1.setMonth(hoje.getMonth() + 1);
    Date mes2 = new Date();
    mes2.setMonth(hoje.getMonth() - 2);
    Date mes3 = new Date();
    mes3.setMonth(hoje.getMonth() - 3);

    est.incluir(p2);

    // Compra de um lote de produtos com validade ok
    est.comprar(2, 10, 2, mes1);

    // Compra de 2 lotes de produtos vencidos
    est.comprar(2, 15, 4, mes2);
    est.comprar(2, 25, 6, mes3);

    Produto maisUm = est.pesquisar(2);
    assertEquals(50, maisUm.getQuantidade());
    assertEquals(4.6, maisUm.getPrecoDeCompra(), 0.001);
    assertEquals(5.52, maisUm.getPrecoDeVenda(), 0.001);

    // Deveria vender 10 do lote com vencimento no mes1
    double val = est.vender(2, 10);
    assertEquals(40, maisUm.getQuantidade());
    assertEquals(55.2, val, 0.001);

    try {
      val = est.vender(2, 20);
      fail("Não Deveria vender pois o resto é vencido.");
    } catch (ProdutoVencido e) {
      // Não Deveria vender pois o resto é vencido
    }
    assertEquals(40, maisUm.getQuantidade());

    // Deveria informar a quantidade de itens vencidos
    assertEquals(40, est.quantidadeVencidos(2));

    ArrayList<Produto> vencidos = est.estoqueVencido();
    assertEquals(1, vencidos.size());

    est.incluir(p3);
    // Compra de 2 lotes de produtos vencidos
    est.comprar(3, 15, 4, mes2);
    vencidos = est.estoqueVencido();
    assertEquals(2, vencidos.size());
  }

  @Test
  public void testarVendaProdutosPereciveisInvalidos()
      throws ProdutoJaCadastrado, ProdutoVencido, ProdutoInexistente,
      DadosInvalidos, ProdutoNaoPerecivel {
    Fornecedor f1 = new Fornecedor(1, "Forn1");
    Produto p2 = new ProdutoPerecivel(2, "Arroz Maria", 10, 0.2, f1);
    Produto p1 = new Produto(1, "coca-cola", 5, 0.5, f1);

    Estoque est = new Estoque();
    est.removerTudo();

    Date hoje = new Date();
    Date mes1 = new Date();
    mes1.setMonth(hoje.getMonth() + 1);
    Date mes2 = new Date();
    mes2.setMonth(hoje.getMonth() - 2);
    Date mes3 = new Date();
    mes3.setMonth(hoje.getMonth() - 3);

    est.incluir(p2);
    est.incluir(p1);

    // Compra de um lote de produtos com validade nula
    try {
      est.comprar(2, 10, 2, null);
      fail("Data invalida");
    } catch (DadosInvalidos e1) {
      // Compra de um lote de produtos com validade nula
    }

    // Compra de um lote de produtos inexistente
    try {
      est.comprar(3, 10, 2, mes1);
      fail("Compra de produto inexistente");
    } catch (ProdutoInexistente e1) {
      // Compra de um lote de produtos inexistente
    }

    // Compra de um lote de produtos nao perecivel com data
    try {
      est.comprar(1, 10, 2, mes1);
      fail("Produto não perecivel");
    } catch (ProdutoNaoPerecivel e1) {
      // Compra de um lote de produtos nao perecivel com data
    }
  }

  @Test
  public void testarEstoqueMinimo()
      throws ProdutoJaCadastrado, DadosInvalidos, ProdutoInexistente,
      ProdutoNaoPerecivel {
    Fornecedor f1 = new Fornecedor(1, "Forn1");
    Produto p1 = new Produto(1, "coca-cola", 100, 3.5, f1);
    Produto p2 = new Produto(2, "omo", 200, 3.5, f1);
    Produto p3 = new Produto(3, "papel", 300, 3.5, f1);
    Produto p4 = new Produto(4, "sabonete", 400, 3.5, f1);
    Produto p5 = new Produto(5, "carne", 500, 3.5, f1);

    Estoque est = new Estoque();
    est.removerTudo();

    est.incluir(p1);
    est.comprar(1, 1, 2.5, null);
    est.incluir(p2);
    est.comprar(2, 1, 2.5, null);
    est.incluir(p3);
    est.comprar(3, 1, 2.5, null);
    est.incluir(p4);
    est.comprar(4, 400, 2.5, null);
    est.incluir(p5);
    est.comprar(5, 500, 2.5, null);

    ArrayList<Produto> minimos = est.estoqueAbaixoDoMinimo();
    for (Produto prod : minimos) {
      if (prod != null) {
        assertTrue(prod.getQuantidade() < prod.getEstoqueMinimo());
      }
    }
    assertEquals(3, minimos.size());
  }
}
