package es.aritzherrero.ejerciciol.Modelo;


import java.sql.Blob;
import java.util.Objects;


public class Aeropuerto {
    private int id;
    private String nombre;
    private int anio_inauguracion;
    private int capacidad;
    private Direccion direccion;
    private Blob imagen;


    public Aeropuerto(int id, String nombre, int anio_inauguracion, int capacidad, Direccion direccion, Blob imagen) {
        this.id = id;
        this.nombre = nombre;
        this.anio_inauguracion = anio_inauguracion;
        this.capacidad = capacidad;
        this.direccion = direccion;
        this.imagen = imagen;
    }


    public Aeropuerto() {}


    @Override
    public String toString() {
        return nombre;
    }


    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }


    public String getNombre() {
        return nombre;
    }


    public void setNombre(String nombre) {
        this.nombre = nombre;
    }


    public int getAnio_inauguracion() {
        return anio_inauguracion;
    }


    public void setAnio_inauguracion(int anio_inauguracion) {
        this.anio_inauguracion = anio_inauguracion;
    }


    public int getCapacidad() {
        return capacidad;
    }


    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }


    public Direccion getDireccion() {
        return direccion;
    }


    public void setDireccion(Direccion direccion) {
        this.direccion = direccion;
    }

    public Blob getImagen() {
        return imagen;
    }


    public void setImagen(Blob imagen) {
        this.imagen = imagen;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Aeropuerto that = (Aeropuerto) o;
        return anio_inauguracion == that.anio_inauguracion && capacidad == that.capacidad && Objects.equals(nombre, that.nombre) && Objects.equals(direccion, that.direccion) && Objects.equals(imagen, that.imagen);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre, anio_inauguracion, capacidad, direccion, imagen);
    }
}