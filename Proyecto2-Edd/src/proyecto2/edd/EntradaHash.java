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

import java.io.Serializable;

public class EntradaHash<K, V> implements Serializable {
    
    K clave;
    V valor;

    EntradaHash(K clave, V valor) {
        this.clave = clave;
        this.valor = valor;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        EntradaHash<?, ?> other = (EntradaHash<?, ?>) obj;
        return this.clave.equals(other.clave);
}
    
}
