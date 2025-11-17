/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto2.edd;

/**
 *
 * @author derek
 * 
 * * Clase auxiliar para almacenar el par Clave-Valor dentro de la Tabla Hash.
 *
 * @param <K> El tipo de la Clave (Key).
 * @param <V> El tipo del Valor (Value).
 */
public class EntradaHash<K, V> {
    
    K clave;
    V valor;

    EntradaHash(K clave, V valor) {
        this.clave = clave;
        this.valor = valor;
    }
    
}
