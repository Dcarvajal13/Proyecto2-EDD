/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto2.edd;

/**
 * Implementación de un Árbol AVL (Árbol Binario de Búsqueda Auto-Balanceable).
 * Esta versión NO utiliza librerías de java.util para sus operaciones
 * principales,
 * en cumplimiento con las reglas del proyecto.
 *
 * @param <T> El tipo de dato a almacenar, debe ser comparable.
 */

public class ArbolAVL<T extends Comparable<T>> {
    
    /**
     * Clase interna que representa un nodo del Árbol AVL.
     */
    private class NodoAVL {
        T dato;
        NodoAVL izquierdo;
        NodoAVL derecho;
        int altura;

        /**
         * Constructor para un nuevo nodo.
         * @param dato El dato a almacenar en el nodo.
         */
        NodoAVL(T dato) {
            this.dato = dato;
            this.altura = 1; // La altura de un nuevo nodo (hoja) es 1.
            this.izquierdo = null;
            this.derecho = null;
        }
    }

    private NodoAVL raiz;

    /**
     * Constructor para un Árbol AVL. Inicializa la raíz como nula.
     */
    public ArbolAVL() {
        this.raiz = null;
    }

    // --- MÉTODOS PÚBLICOS PRINCIPALES ---

    /**
     * Inserta un nuevo dato en el árbol AVL.
     * Si el dato ya existe, la operación se ignora.
     *
     * @param dato El dato a insertar.
     */
    public void insertar(T dato) {
        if (dato == null) {
            return; // No se permiten datos nulos
        }
        this.raiz = insertarRecursivo(this.raiz, dato);
    }

    /**
     * Busca un dato en el árbol.
     *
     * @param dato El dato a buscar.
     * @return El dato encontrado en el árbol, o null si no se encuentra.
     */
    public T buscar(T dato) {
        NodoAVL nodo = buscarRecursivo(this.raiz, dato);
        return (nodo == null) ? null : nodo.dato;
    }

    /**
     * Devuelve una ListaSimple con los elementos del árbol ordenados 
     * alfabéticamente (recorrido In-Orden).
     * Esta operación tiene una complejidad O(n).
     *
     * @return Una ListaSimple<T> con los elementos ordenados.
     */
    public ListaSimple<T> getListaInorden() {
        // Usa nuestra propia clase ListaSimple
        ListaSimple<T> lista = new ListaSimple<>();
        recorridoInorden(this.raiz, lista);
        return lista;
    }

    // --- MÉTODOS PRIVADOS RECURSIVOS ---

    /**
     * Método recursivo para insertar un dato y rebalancear el árbol.
     *
     * @param nodo El nodo actual en la recursión.
     * @param dato El dato a insertar.
     * @return El nuevo nodo raíz del subárbol (potencialmente rebalanceado).
     */
    private NodoAVL insertarRecursivo(NodoAVL nodo, T dato) {
        // 1. Inserción estándar de Árbol Binario de Búsqueda (ABB)
        if (nodo == null) {
            return new NodoAVL(dato);
        }

        int comparacion = dato.compareTo(nodo.dato);

        if (comparacion < 0) {
            nodo.izquierdo = insertarRecursivo(nodo.izquierdo, dato);
        } else if (comparacion > 0) {
            nodo.derecho = insertarRecursivo(nodo.derecho, dato);
        } else {
            // Dato duplicado. No se inserta.
            return nodo;
        }

        // 2. Actualizar la altura del nodo actual
        actualizarAltura(nodo);

        // 3. Obtener el Factor de Equilibrio (FE)
        int fe = obtenerFactorEquilibrio(nodo);

        // 4. Rebalancear si es necesario (4 casos)

        // Caso Izquierda-Izquierda (LL)
        if (fe > 1 && obtenerFactorEquilibrio(nodo.izquierdo) >= 0) {
            return rotacionDerecha(nodo);
        }

        // Caso Derecha-Derecha (RR)
        if (fe < -1 && obtenerFactorEquilibrio(nodo.derecho) <= 0) {
            return rotacionIzquierda(nodo);
        }

        // Caso Izquierda-Derecha (LR)
        if (fe > 1 && obtenerFactorEquilibrio(nodo.izquierdo) < 0) {
            nodo.izquierdo = rotacionIzquierda(nodo.izquierdo);
            return rotacionDerecha(nodo);
        }

        // Caso Derecha-Izquierda (RL)
        if (fe < -1 && obtenerFactorEquilibrio(nodo.derecho) > 0) {
            nodo.derecho = rotacionDerecha(nodo.derecho);
            return rotacionIzquierda(nodo);
        }

        // 5. Retornar el nodo (sin cambios o ya balanceado)
        return nodo;
    }

    /**
     * Método recursivo para buscar un dato.
     *
     * @param nodo El nodo actual.
     * @param dato El dato a buscar.
     * @return El nodo que contiene el dato, o null si no se encuentra.
     */
    private NodoAVL buscarRecursivo(NodoAVL nodo, T dato) {
        if (nodo == null) {
            return null; // No encontrado
        }

        int comparacion = dato.compareTo(nodo.dato);

        if (comparacion < 0) {
            return buscarRecursivo(nodo.izquierdo, dato);
        } else if (comparacion > 0) {
            return buscarRecursivo(nodo.derecho, dato);
        } else {
            return nodo; // Encontrado
        }
    }

    /**
     * Método recursivo para el recorrido In-Orden (Izquierdo, Raíz, Derecho).
     *
     * @param nodo El nodo actual.
     * @param lista La ListaSimple donde se agregan los elementos.
     */
    private void recorridoInorden(NodoAVL nodo, ListaSimple<T> lista) {
        if (nodo != null) {
            recorridoInorden(nodo.izquierdo, lista);
            lista.agregar(nodo.dato); // Usa el método .agregar() de nuestra lista
            recorridoInorden(nodo.derecho, lista);
        }
    }

    // --- MÉTODOS AUXILIARES DE BALANCEO ---

    /**
     * Obtiene la altura de un nodo.
     *
     * @param nodo El nodo.
     * @return La altura del nodo, o 0 si el nodo es null.
     */
    private int obtenerAltura(NodoAVL nodo) {
        if (nodo == null) {
            return 0;
        }
        return nodo.altura;
    }

    /**
     * Actualiza la altura de un nodo basándose en la altura de sus hijos.
     *
     * @param nodo El nodo a actualizar.
     */
    private void actualizarAltura(NodoAVL nodo) {
        if (nodo != null) {
            nodo.altura = 1 + Math.max(obtenerAltura(nodo.izquierdo), obtenerAltura(nodo.derecho));
        }
    }

    /**
     * Calcula el Factor de Equilibrio (FE) de un nodo.
     * FE = altura(hijoIzquierdo) - altura(hijoDerecho)
     *
     * @param nodo El nodo a calcular.
     * @return El factor de equilibrio.
     */
    private int obtenerFactorEquilibrio(NodoAVL nodo) {
        if (nodo == null) {
            return 0;
        }
        return obtenerAltura(nodo.izquierdo) - obtenerAltura(nodo.derecho);
    }

    /**
     * Realiza una rotación simple a la derecha (Caso LL).
     *
     * @param z El nodo desbalanceado (+2).
     * @return El nuevo nodo raíz del subárbol.
     */
    private NodoAVL rotacionDerecha(NodoAVL z) {
        NodoAVL y = z.izquierdo;
        NodoAVL T3 = y.derecho;

        // Realizar rotación
        y.derecho = z;
        z.izquierdo = T3;

        // Actualizar alturas (¡Importante: primero Z, luego Y!)
        actualizarAltura(z);
        actualizarAltura(y);

        return y; // y es la nueva raíz
    }

    /**
     * Realiza una rotación simple a la izquierda (Caso RR).
     *
     * @param z El nodo desbalanceado (-2).
     * @return El nuevo nodo raíz del subárbol.
     */
    private NodoAVL rotacionIzquierda(NodoAVL z) {
        NodoAVL y = z.derecho;
        NodoAVL T2 = y.izquierdo;

        // Realizar rotación
        y.izquierdo = z;
        z.derecho = T2;

        // Actualizar alturas (¡Importante: primero Z, luego Y!)
        actualizarAltura(z);
        actualizarAltura(y);

        return y; // y es la nueva raíz
    }

    // --- MÉTODO DE PRUEBA (MAIN) ---

    /**
     * Método main para probar la implementación del Árbol AVL.
     */
    public static void main(String[] args) {
        // Prueba 5: Strings (como en el proyecto)
        ArbolAVL<String> arbolAutores = new ArbolAVL<>();
        arbolAutores.insertar("Carmona, Rhadamés");
        arbolAutores.insertar("Guillén-Drija, Christian");
        arbolAutores.insertar("Pérez, Andrea");
        arbolAutores.insertar("Loscher, Iván");
        arbolAutores.insertar("Sandoval, Raquel");

        System.out.println("\n--- Lista de Autores Ordenada (usando ListaSimple) ---");
        ListaSimple<String> autoresOrdenados = arbolAutores.getListaInorden();
        
        // Recorremos nuestra ListaSimple para imprimir
        for (int i = 0; i < autoresOrdenados.getTamano(); i++) {
            System.out.println(autoresOrdenados.get(i));
        }
        
        // Prueba de búsqueda
        System.out.println("\nBuscando 'Loscher, Iván': " + arbolAutores.buscar("Loscher, Iván"));
        System.out.println("Buscando 'Pérez, María': " + arbolAutores.buscar("Pérez, María"));
    }
    
}
