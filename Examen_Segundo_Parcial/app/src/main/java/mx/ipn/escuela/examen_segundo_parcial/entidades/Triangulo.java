package mx.ipn.escuela.examen_segundo_parcial.entidades;

import java.io.Serializable;

public class Triangulo implements Serializable {

    private int id;
    private int color_rojo;
    private int color_verde;
    private int color_azul;
    private int iteraciones;
    private String tipo_triangulo;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getColor_rojo() {
        return color_rojo;
    }

    public void setColor_rojo(int color_rojo) {
        this.color_rojo = color_rojo;
    }

    public int getColor_verde() {
        return color_verde;
    }

    public void setColor_verde(int color_verde) {
        this.color_verde = color_verde;
    }

    public int getColor_azul() {
        return color_azul;
    }

    public void setColor_azul(int color_azul) {
        this.color_azul = color_azul;
    }

    public int getIteraciones() {
        return iteraciones;
    }

    public void setIteraciones(int iteraciones) {
        this.iteraciones = iteraciones;
    }

    public String getTipo_triangulo() {
        return tipo_triangulo;
    }

    public void setTipo_triangulo(String tipo_triangulo) {
        this.tipo_triangulo = tipo_triangulo;
    }
}