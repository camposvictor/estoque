package victorHugoDeSouzaCampos;

import java.util.ArrayList;
import java.util.Date;

import victorHugoDeSouzaCampos.exceptions.*;

public class ProdutoPerecivel extends Produto {
  private ArrayList<Lote> lotes;

  public ProdutoPerecivel() {
  }

  public ProdutoPerecivel(int cod, String desc, int min, double lucro, Fornecedor forne) throws DadosInvalidos {
    super(cod, desc, min, lucro, forne);
    this.lotes = new ArrayList<Lote>();
  }

  public ArrayList<Lote> getLotes() {
    return lotes;
  }

  public void setLotes(ArrayList<Lote> lotes) {
    this.lotes = lotes;
  }

  private Lote lotePertoDeVencer() {
    Lote lotePertoDeVencer = null;
    for (Lote lote : lotes) {

      boolean estaVencido = lote.getValidade().before(new Date());
      if (estaVencido)
        continue;

      if (lotePertoDeVencer == null) {
        lotePertoDeVencer = lote;
        continue;
      }

      boolean estaMaisPertoDeVencer = lote.getValidade().before(lotePertoDeVencer.getValidade());
      if (estaMaisPertoDeVencer)
        lotePertoDeVencer = lote;

    }
    return lotePertoDeVencer;
  }

  public void compra(int quant, double val, Date validade) throws DadosInvalidos {
    verificarNaoNulo(validade);
    super.compra(quant, val);

    Lote lote = new Lote(quant, validade);
    this.lotes.add(lote);
  }

  @Override
  double venda(int quant) throws DadosInvalidos, ProdutoVencido {
    if (quant > this.quantidade) {
      throw new DadosInvalidos();
    }
    if (quant > this.quantidade - this.vencidos()) {
      throw new ProdutoVencido();
    }
    int quantidadeRestante = quant;
    Lote lotePertoDeVencer = lotePertoDeVencer();

    while (quantidadeRestante > 0) {
      int quantidadeParaVender = Math.min(quantidadeRestante, lotePertoDeVencer.getQuant());

      if (lotePertoDeVencer.getQuant() == quantidadeParaVender) {
        this.lotes.remove(lotePertoDeVencer);
      } else {
        lotePertoDeVencer.setQuant(lotePertoDeVencer.getQuant() - quantidadeParaVender);
      }
      quantidadeRestante -= quantidadeParaVender;
      lotePertoDeVencer = lotePertoDeVencer();
    }

    this.quantidade -= quant;
    return this.precoDeVenda * quant;
  }

  public int vencidos() {
    int vencidos = 0;
    for (Lote lote : lotes) {
      if (lote.getValidade().before(new Date())) {
        vencidos += lote.getQuant();
      }
    }
    return vencidos;
  }
}
