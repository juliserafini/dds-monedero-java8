package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Cuenta {

  private double saldo;
  private List<Movimiento> movimientos = new ArrayList<>();

  //El atributo saldo lo inicializa dos veces y por eso lo deje solo dentro del constructor
  public Cuenta() {
    saldo = 0;
  }

  public Cuenta(double montoInicial) {
    saldo = montoInicial;
  }

  public void setMovimientos(List<Movimiento> movimientos) {
    this.movimientos = movimientos;
  }
// Los metodos sacar y poner repiten codigo y ambos son metodos largos que se pueden descomponer DUPLICATED CODE y LONG METHOD

  public void poner(double cuanto) {
    chequearMontoNegativo(cuanto);
    chequearMaximaCntDepositos();
    Movimiento nuevoMov = new Movimiento(LocalDate.now(), cuanto, true);
    agregarMovimiento(nuevoMov);
  }

  public void sacar(double cuanto) {
    chequearMontoNegativo(cuanto);
    chequearSaldoMenor(cuanto);
    chequearElLimite(cuanto);
    Movimiento nuevoMov = new Movimiento(LocalDate.now(), cuanto, true);
    agregarMovimiento(nuevoMov);
  }

  public void agregarMovimiento(Movimiento movimiento) {
    movimientos.add(movimiento);
    calcularValor(movimiento);

  }
  public void calcularValor(Movimiento movimiento) {
    if (movimiento.isDeposito()) { saldo +=  movimiento.getMonto();
    } else {
      saldo -=  movimiento.getMonto();
    }
  }
// El metodo getMontoExtraido tambien comete un code smell de tipo LONG METHOD dentro del filter y FEATURE ENVY ya que toca muchas veces el movimiento
  public double getMontoExtraidoA(LocalDate fecha) {
    return getMovimientos().stream()
        .filter(movimiento -> movimiento.fueExtraido(fecha))
        .mapToDouble(Movimiento::getMonto)
        .sum();
  }

  public List<Movimiento> getMovimientos() {
    return movimientos;
  }

  public double getSaldo() {
    return saldo;
  }

  public void setSaldo(double saldo) {
    this.saldo = saldo;
  }

  public void chequearMontoNegativo(double cuanto){
    if (cuanto <= 0) {
      throw new MontoNegativoException(cuanto + ": el monto a ingresar debe ser un valor positivo");
    }
  }
  public void chequearMaximaCntDepositos(){
    if (getMovimientos().stream().filter(movimiento -> movimiento.isDeposito()).count() >= 3) {
      throw new MaximaCantidadDepositosException("Ya excedio los " + 3 + " depositos diarios");
    }
  }
  public void chequearSaldoMenor(double cuanto){
    if (getSaldo() - cuanto < 0) {
      throw new SaldoMenorException("No puede sacar mas de " + getSaldo() + " $");
    }
  }
  public void chequearElLimite(double cuanto){
    double montoExtraidoHoy = getMontoExtraidoA(LocalDate.now());
    double limite = 1000 - montoExtraidoHoy;
    if (cuanto > limite) {
      throw new MaximoExtraccionDiarioException("No puede extraer mas de $ " + 1000
          + " diarios, límite: " + limite);
    }
  }

}
