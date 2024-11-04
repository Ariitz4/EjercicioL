package es.aritzherrero.ejerciciol.Modelo;

import java.util.Objects;


public class AeropuertoPrivado {
    private Aeropuerto aeropuerto;
    private int numero_socios;


    public AeropuertoPrivado(Aeropuerto aeropuerto, int numero_socios) {
        this.aeropuerto = aeropuerto;
        this.numero_socios = numero_socios;
    }


    public AeropuertoPrivado() {}


    public Aeropuerto getAeropuerto() {
        return aeropuerto;
    }


    public void setAeropuerto(Aeropuerto aeropuerto) {
        this.aeropuerto = aeropuerto;
    }


    public int getNumero_socios() {
        return numero_socios;
    }


    public void setNumero_socios(int numero_socios) {
        this.numero_socios = numero_socios;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AeropuertoPrivado that = (AeropuertoPrivado) o;
        return Objects.equals(aeropuerto, that.aeropuerto);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(aeropuerto);
    }
}
