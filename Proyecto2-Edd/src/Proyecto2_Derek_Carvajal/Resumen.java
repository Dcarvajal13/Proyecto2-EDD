/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Proyecto2_Derek_Carvajal;

/**
 *
 * @author derek
 * 
 * * Clase Modelo que representa una Investigación o Resumen
 * Almacena título, autores, cuerpo del resumen y palabras clave
 * 
 */

import java.io.Serializable;

public class Resumen implements Serializable {
    
    private String titulo;
    private ListaSimple<String> autores;
    private String cuerpoResumen;
    private ListaSimple<String> palabrasClave;

    /**
     * Constructor para la clase Resumen.
     *
     * @param titulo El título de la investigación.
     * @param autores Una ListaSimple de los nombres de los autores.
     * @param cuerpoResumen El texto completo del resumen.
     * @param palabrasClave Una ListaSimple de las palabras clave asociadas.
     */
    public Resumen(String titulo, ListaSimple<String> autores, String cuerpoResumen, ListaSimple<String> palabrasClave) {
        this.titulo = titulo;
        this.autores = autores;
        this.cuerpoResumen = cuerpoResumen;
        this.palabrasClave = palabrasClave;
    }
    
   
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final Resumen other = (Resumen) obj;
        // Comparamos por título (ignorando mayúsculas)
        return this.titulo.equalsIgnoreCase(other.titulo);
    }

    // --- Getters ---
    // (Necesitarás getters para que la interfaz gráfica pueda mostrar los datos)

    public String getTitulo() {
        return titulo;
    }

    public ListaSimple<String> getAutores() {
        return autores;
    }

    public String getCuerpoResumen() {
        return cuerpoResumen;
    }

    public ListaSimple<String> getPalabrasClave() {
        return palabrasClave;
    }

    // (Opcional: un método 'toString' para pruebas de consola)
    @Override
    public String toString() {
        return "Resumen: " + this.titulo;
    }

    
    
    
}
