package es.aritzherrero.ejerciciol.Modelo;


import java.math.BigDecimal;
import java.util.Objects;


public class AeropuertoPublico {
    private Aeropuerto aeropuerto;
    private BigDecimal financiacion;
    private int num_trabajadores;


    public AeropuertoPublico(Aeropuerto aeropuerto, BigDecimal financiacion, int num_trabajadores) {
        this.aeropuerto = aeropuerto;
        this.financiacion = financiacion;
        this.num_trabajadores = num_trabajadores;
    }

    public AeropuertoPublico() {}


    public Aeropuerto getAeropuerto() {
        return aeropuerto;
    }


    public void setAeropuerto(Aeropuerto aeropuerto) {
        this.aeropuerto = aeropuerto;
    }


    public BigDecimal getFinanciacion() {
        return financiacion;
    }


    public void setFinanciacion(BigDecimal financiacion) {
        this.financiacion = financiacion;
    }


    public int getNum_trabajadores() {
        return num_trabajadores;
    }


    public void setNum_trabajadores(int num_trabajadores) {
        this.num_trabajadores = num_trabajadores;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AeropuertoPublico that = (AeropuertoPublico) o;
        return Objects.equals(aeropuerto, that.aeropuerto);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(aeropuerto);
    }
}
