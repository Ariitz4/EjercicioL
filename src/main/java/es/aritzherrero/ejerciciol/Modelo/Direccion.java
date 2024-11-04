package es.aritzherrero.ejerciciol.Modelo;

import java.util.Objects;


public class Direccion {
    private int id;
    private String pais;
    private String ciudad;
    private String calle;
    private int numero;


    public Direccion(int id, String pais, String ciudad, String calle, int numero) {
        this.id = id;
        this.pais = pais;
        this.ciudad = ciudad;
        this.calle = calle;
        this.numero = numero;
    }


    public Direccion() {}


    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }


    public String getPais() {
        return pais;
    }


    public void setPais(String pais) {
        this.pais = pais;
    }


    public String getCiudad() {
        return ciudad;
    }


    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }


    public String getCalle() {
        return calle;
    }


    public void setCalle(String calle) {
        this.calle = calle;
    }


    public int getNumero() {
        return numero;
    }


    public void setNumero(int numero) {
        this.numero = numero;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Direccion direccion = (Direccion) o;
        return numero == direccion.numero && Objects.equals(pais, direccion.pais) && Objects.equals(ciudad, direccion.ciudad) && Objects.equals(calle, direccion.calle);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pais, ciudad, calle, numero);
    }
}