/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto2.edd;

/**
 *
 * @author derek
 * 
 * * Clase Modelo que representa a un Autor
 * Esta clase se almacenará en el ArbolAVL de Autores
 * Debe implementar Comparable para ser ordenada por el AVL
 */

import java.io.Serializable;

public class Autor implements Comparable<Autor>, Serializable{
    
    private String nombre;
    private ListaSimple<Resumen> resumenes; // Lista de resúmenes de este autor

    /**
     * Constructor para la clase Autor.
     * @param nombre El nombre del autor.
     */
    public Autor(String nombre) {
        // CORRECCIÓN 1: Limpiamos espacios al crear el autor
        if (nombre != null) {
            this.nombre = nombre.trim(); 
        } else {
            this.nombre = "";
        }
        this.resumenes = new ListaSimple<>();
    }

    // --- Getters ---
    public String getNombre() {
        return nombre;
    }

    public ListaSimple<Resumen> getResumenes() {
        return resumenes;
    }

    /**
     * Agrega un resumen a la lista de publicaciones de este autor.
     */
    public void agregarResumen(Resumen resumen) {
        // CORRECCIÓN 2: Validar que no agreguemos el mismo resumen dos veces
        // al mismo autor (por seguridad).
        for (int i = 0; i < this.resumenes.getTamano(); i++) {
            if (this.resumenes.get(i).getTitulo().equalsIgnoreCase(resumen.getTitulo())) {
                return; // Ya tiene este resumen, no hacemos nada
            }
        }
        this.resumenes.agregar(resumen);
    }

    /**
     * Método de comparación (compareTo) requerido por la interfaz Comparable.
     * Permite al ArbolAVL ordenar los autores alfabéticamente por nombre.
     *
     * @param otroAutor El otro Autor con el que se va a comparar.
     * @return un entero negativo, cero, o positivo si este nombre es
     * menor, igual, o mayor que el del otroAutor.
     */
    @Override
    public int compareTo(Autor otroAutor) {
        return this.nombre.compareToIgnoreCase(otroAutor.nombre);
    }

    // Redefinimos equals y hashCode para búsquedas (buena práctica)
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Autor autor = (Autor) obj;
        return this.nombre.equals(autor.nombre);
    }

    @Override
    public int hashCode() {
        return this.nombre.hashCode();
    }
    
}
