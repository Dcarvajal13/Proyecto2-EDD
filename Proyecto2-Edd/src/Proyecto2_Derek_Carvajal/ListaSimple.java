/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Proyecto2_Derek_Carvajal;

/**
 * Implementación de una Lista Enlazada Simple (TDA auxiliar).
 * Esto reemplaza la necesidad de usar java.util.ArrayList o java.util.LinkedList.
 *
 * @param <T> El tipo de dato a almacenar.
 */

import java.io.Serializable;

public class ListaSimple<T> implements Serializable {

    /**
     * Clase interna que representa un nodo de la lista.
     */
    private class NodoLista implements Serializable{
        T dato;
        NodoLista siguiente;

        NodoLista(T dato) {
            this.dato = dato;
            this.siguiente = null;
        }
    }
    
    public boolean eliminar(T dato) {
        if (cabeza == null) return false;

        if (cabeza.dato.equals(dato)) {
            cabeza = cabeza.siguiente;
            tamano--;
            return true;
        }

        NodoLista actual = cabeza;
        while (actual.siguiente != null) {
            if (actual.siguiente.dato.equals(dato)) {
                actual.siguiente = actual.siguiente.siguiente;
                tamano--;
                return true;
            }
            actual = actual.siguiente;
        }
        return false;
    }

    private NodoLista cabeza;
    private int tamano;

    /**
     * Constructor de la ListaSimple.
     */
    public ListaSimple() {
        this.cabeza = null;
        this.tamano = 0;
    }

    /**
     * Agrega un elemento al final de la lista.
     *
     * @param dato El dato a agregar.
     */
    public void agregar(T dato) {
        NodoLista nuevoNodo = new NodoLista(dato);
        if (this.cabeza == null) {
            this.cabeza = nuevoNodo;
        } else {
            NodoLista actual = this.cabeza;
            while (actual.siguiente != null) {
                actual = actual.siguiente;
            }
            actual.siguiente = nuevoNodo;
        }
        this.tamano++;
    }

    /**
     * Obtiene un elemento en una posición específica.
     *
     * @param index El índice del elemento (0-based).
     * @return El dato en esa posición.
     * @throws IndexOutOfBoundsException si el índice está fuera de rango.
     */
    public T get(int index) {
        if (index < 0 || index >= this.tamano) {
            throw new IndexOutOfBoundsException("Índice fuera de rango: " + index);
        }
        NodoLista actual = this.cabeza;
        for (int i = 0; i < index; i++) {
            actual = actual.siguiente;
        }
        return actual.dato;
    }

    /**
     * Devuelve el número de elementos en la lista.
     *
     * @return El tamaño de la lista.
     */
    public int getTamano() {
        return this.tamano;
    }

    /**
     * Verifica si la lista está vacía.
     *
     * @return true si la lista está vacía, false en caso contrario.
     */
    public boolean estaVacia() {
        return this.tamano == 0;
    }
    
}
