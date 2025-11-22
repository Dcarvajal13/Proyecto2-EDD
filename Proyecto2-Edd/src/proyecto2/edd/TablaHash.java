/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto2.edd;

/**
 *
 * @author derek
 * * Implementación de una Tabla de Dispersión (Hash Table) con Encadenamiento 
 * Separado.
 * Esta clase NO utiliza librerías de java.util para sus estructuras.
 * Utiliza la clase 'ListaSimple' como TDA auxiliar para manejar colisiones.
 *
 * @param <K> El tipo de la Clave (debe tener un buen .toString()).
 * @param <V> El tipo del Valor a almacenar.
 */

import java.io.Serializable;

public class TablaHash<K, V> implements Serializable{
    
    private ListaSimple<EntradaHash<K, V>>[] tabla;
    private int tamanoActual; // Número de elementos insertados
    private int tamanoArreglo; // Tamaño del arreglo (M)

    /**
     * Constructor que inicializa la Tabla Hash.
     *
     * @param tamanoArreglo El tamaño del arreglo subyacente (se recomienda un
     * número primo).
     */
    public TablaHash(int tamanoArreglo) {
        this.tamanoArreglo = tamanoArreglo;
        this.tamanoActual = 0;
        // Inicializa el arreglo principal
        this.tabla = (ListaSimple<EntradaHash<K, V>>[]) new ListaSimple[tamanoArreglo];

        // ¡Importante! Inicializa una lista vacía en cada posición del arreglo
        // para evitar NullPointerException.
        for (int i = 0; i < tamanoArreglo; i++) {
            this.tabla[i] = new ListaSimple<>();
        }
    }

    /**
     * Función Hash diseñada por el estudiante (requisito del proyecto).
     * Convierte la clave en un índice del arreglo.
     *
     * @param clave La clave a "hashear".
     * @return Un índice dentro del rango [0, tamanoArreglo - 1].
     */
    private int funcionHash(K clave) {
        // Convierte la clave a String para procesarla
        String claveStr = clave.toString();
        long hash = 0;

        // Algoritmo simple: suma ponderada de los caracteres
        // (Similar al método de Horner)
        for (int i = 0; i < claveStr.length(); i++) {
            // Se multiplica por un primo (31) para mejorar la dispersión
            hash = (31 * hash + claveStr.charAt(i)); 
        }

        // 1. Asegura que el hash sea positivo
        hash = Math.abs(hash); 
        // 2. Aplica módulo para que caiga dentro del tamaño del arreglo
        return (int) (hash % this.tamanoArreglo);
    }
    
    /**
     * Elimina un valor asociado a una clave.
     * @param clave La clave a eliminar.
     * @return true si se eliminó, false si no existía.
     */
    public boolean eliminar(K clave) {
        int indice = funcionHash(clave);
        ListaSimple<EntradaHash<K, V>> lista = this.tabla[indice];

        // Necesitamos buscar la entrada específica para eliminarla
        for (int i = 0; i < lista.getTamano(); i++) {
            EntradaHash<K, V> entrada = lista.get(i);
            if (entrada.clave.equals(clave)) {
                // Usamos el método eliminar de la lista que acabamos de crear
                lista.eliminar(entrada);
                this.tamanoActual--;
                return true;
            }
        }
        return false;
    }

    /**
     * Inserta un nuevo par clave-valor en la tabla.
     * No permite claves duplicadas (requisito del proyecto).
     *
     * @param clave La clave (ej: título del resumen).
     * @param valor El valor (ej: el objeto Resumen).
     * @return true si la inserción fue exitosa, false si la clave ya existía.
     */
    public boolean insertar(K clave, V valor) {
        int indice = funcionHash(clave);
        ListaSimple<EntradaHash<K, V>> lista = this.tabla[indice];

        // 1. Validar que no se introduzcan duplicados [cite: 21]
        // Recorremos la lista en ese índice
        for (int i = 0; i < lista.getTamano(); i++) {
            EntradaHash<K, V> entrada = lista.get(i);
            if (entrada.clave.equals(clave)) {
                // La clave ya existe. No se inserta.
                return false; 
            }
        }

        // 2. Si no existe, se agrega
        EntradaHash<K, V> nuevaEntrada = new EntradaHash<>(clave, valor);
        lista.agregar(nuevaEntrada);
        this.tamanoActual++;
        return true;
    }

    /**
     * Busca un valor en la tabla a partir de su clave.
     * Esta operación debe ser O(1) en promedio.
     *
     * @param clave La clave a buscar.
     * @return El valor asociado a la clave, o null si la clave no se encuentra.
     */
    public V buscar(K clave) {
        int indice = funcionHash(clave);
        ListaSimple<EntradaHash<K, V>> lista = this.tabla[indice];

        // Recorremos la lista en el índice correspondiente
        for (int i = 0; i < lista.getTamano(); i++) {
            EntradaHash<K, V> entrada = lista.get(i);
            if (entrada.clave.equals(clave)) {
                // ¡Encontrado!
                return entrada.valor;
            }
        }

        // No se encontró en la lista
        return null;
    }

    /**
     * Devuelve el número de elementos almacenados en la tabla.
     * @return El número de elementos.
     */
    public int getTamanoActual() {
        return this.tamanoActual;
    }

    // --- MÉTODO DE PRUEBA (MAIN) ---

    /**
     * Método main para probar la implementación de la TablaHash.
     */
    public static void main(String[] args) {
        // Usamos un tamaño de arreglo pequeño (primo) para forzar colisiones
        TablaHash<String, String> miTabla = new TablaHash<>(7);

        // Claves de prueba (usaremos objetos Resumen en el proyecto real)
        String titulo1 = "Interacción inalámbrica con dispositivos de bajo costo"; // [cite: 117]
        String valor1 = "Resumen de Rhadamés Carmona...";

        String titulo2 = "Complemento de software para el diseño de fundaciones."; // [cite: 130]
        String valor2 = "Resumen de Raquel Sandoval...";

        String titulo3 = "Arquitectura referencial para mecanismos de Internacionalización"; // [cite: 145]
        String valor3 = "Resumen de Christian Guillén...";

        // Insertamos
        System.out.println("Insertando '" + titulo1 + "': " + miTabla.insertar(titulo1, valor1));
        System.out.println("Insertando '" + titulo2 + "': " + miTabla.insertar(titulo2, valor2));
        System.out.println("Insertando '" + titulo3 + "': " + miTabla.insertar(titulo3, valor3));

        System.out.println("Total de elementos: " + miTabla.getTamanoActual());

        // Probamos insertar un duplicado
        System.out.println("\nIntentando insertar duplicado de '" + titulo2 + "': " + miTabla.insertar(titulo2, "Otro valor"));
        System.out.println("Total de elementos: " + miTabla.getTamanoActual()); // Debe ser 3

        // Probamos las búsquedas
        System.out.println("\n--- Pruebas de Búsqueda ---");
        System.out.println("Buscando '" + titulo1 + "':");
        System.out.println(miTabla.buscar(titulo1));

        System.out.println("\nBuscando 'Un título que no existe':");
        System.out.println(miTabla.buscar("Un título que no existe"));
    }
    
    public ListaSimple<K> obtenerTodasLasClaves() {
        ListaSimple<K> claves = new ListaSimple<>();
        if (tabla != null) {
            for (int i = 0; i < tabla.length; i++) {
                if (tabla[i] != null) {
                    for (int j = 0; j < tabla[i].getTamano(); j++) {
                        claves.agregar(tabla[i].get(j).clave);
                    }
                }
            }
        }
        return claves;
    }
    
 
    
    }

    
    
    

