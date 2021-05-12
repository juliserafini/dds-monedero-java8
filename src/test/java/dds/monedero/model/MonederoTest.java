package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MonederoTest {
  private Cuenta cuenta;
  private LocalDate fecha = LocalDate.of(2022, 01, 24);
  private Movimiento movimientoNoDeposito = new Movimiento(fecha, 1200, false);
  private Movimiento movimientoDeposito = new Movimiento(fecha, 400, true);
  @BeforeEach
  void init() {
    cuenta = new Cuenta();
  }

  @Test
  void PonerSaldoEnUnaCuenta() {
    cuenta.poner(1500);
    assertEquals(cuenta.getSaldo(), 1500);
  }


  @Test
  void PonerMontoNegativo() {
    assertThrows(MontoNegativoException.class, () -> cuenta.poner(-1500));
  }

  @Test
  void HacerTresDepositos() {
    cuenta.poner(1500);
    cuenta.poner(456);
    cuenta.poner(1900);
    assertEquals(cuenta.getSaldo(), 3856);
  }

  @Test
  void MasDeTresDepositos() {
    assertThrows(MaximaCantidadDepositosException.class, () -> {
          cuenta.poner(1500);
          cuenta.poner(456);
          cuenta.poner(1900);
          cuenta.poner(245);
    });
  }

  @Test
  void ExtraerMasQueElSaldo() {
    assertThrows(SaldoMenorException.class, () -> {
          cuenta.setSaldo(90);
          cuenta.sacar(1001);
    });
  }

  @Test
  public void ExtraerMasDe1000() {
    assertThrows(MaximoExtraccionDiarioException.class, () -> {
      cuenta.setSaldo(5000);
      cuenta.sacar(1001);
    });
  }

  @Test
  public void ExtraerMontoNegativo() {
    assertThrows(MontoNegativoException.class, () -> cuenta.sacar(-500));
  }

  @Test
  public void SacarDineroDeUnaCuentaCon2000(){
    cuenta.poner(2000);
    cuenta.sacar(800);
    assertEquals(cuenta.getSaldo(), 1200);
  }
  @Test
  public void AgregarUnMovimientoQueSeExtrajoAUnaCuentaCon2000deSaldo(){
    cuenta.setSaldo(2000);
    cuenta.agregarMovimiento(movimientoNoDeposito);
    assertEquals(cuenta.getMontoExtraidoA(fecha), 1200);
  }

  @Test
  public void AgregarUnMovimientoQueEsDepositoAUnaCuentaCon1000deSaldo(){
    cuenta.setSaldo(1000);
    cuenta.agregarMovimiento(movimientoDeposito);
    assertEquals(cuenta.getSaldo(), 1400);
  }

  @Test
  public void VerificarQueLosMovimientosEstanEnLaCuenta(){
    cuenta.agregarMovimiento(movimientoNoDeposito);
    cuenta.agregarMovimiento(movimientoDeposito);
    assertTrue(cuenta.getMovimientos().contains(movimientoNoDeposito));
    assertTrue(cuenta.getMovimientos().contains(movimientoDeposito));
  }





}