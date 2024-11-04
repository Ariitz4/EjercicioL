package es.aritzherrero.ejerciciol.Modelo;

import java.util.Objects;


public class Avion {
    private int id;
    private String modelo;
    private int numero_asientos;
    private int velocidad_maxima;
    private boolean activado;
    private Aeropuerto aeropuerto;


    public Avion(int id, String modelo, int numero_asientos, int velocidad_maxima, boolean activado, Aeropuerto aeropuerto) {
        this.id = id;
        this.modelo = modelo;
        this.numero_asientos = numero_asientos;
        this.velocidad_maxima = velocidad_maxima;
        this.activado = activado;
        this.aeropuerto = aeropuerto;
    }


    public Avion() {}


    @Override
    public String toString() {
        return modelo;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getModelo() {
        return modelo;
    }


    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public int getNumero_asientos() {
        return numero_asientos;
    }


    public void setNumero_asientos(int numero_asientos) {
        this.numero_asientos = numero_asientos;
    }


    public int getVelocidad_maxima() {
        return velocidad_maxima;
    }


    public void setVelocidad_maxima(int velocidad_maxima) {
        this.velocidad_maxima = velocidad_maxima;
    }


    public boolean isActivado() {
        return activado;
    }


    public void setActivado(boolean activado) {
        this.activado = activado;
    }

    public Aeropuerto getAeropuerto() {
        return aeropuerto;
    }

    public void setAeropuerto(Aeropuerto aeropuerto) {
        this.aeropuerto = aeropuerto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Avion avion = (Avion) o;
        return Objects.equals(modelo, avion.modelo) && Objects.equals(aeropuerto, avion.aeropuerto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(modelo, aeropuerto);
    }

}
