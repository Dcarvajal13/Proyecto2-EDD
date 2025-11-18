/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto2.edd;

/**
 *
 * @author derek
 * * Clase "Cerebro" del sistema
 * Contiene todas las estructuras de datos principales y la lógica de negocio
 * para gestionar las investigaciones
 */


import java.io.*;
import java.io.Serializable;

public class GestorInvestigaciones implements Serializable{
    
    // --- 1. ESTRUCTURAS DE DATOS PRINCIPALES ---

    /**
     * Repositorio Principal (Req. 1):
     * Almacena todos los Resúmenes.
     * Clave: Título del Resumen (String)
     * Valor: Objeto Resumen
     * Complejidad esperada: O(1) para búsqueda/inserción.
     */
    private TablaHash<String, Resumen> repositorioResumenes;

    /**
     * Índice de Autores (Req. 4):
     * Almacena un objeto Autor único por cada autor para listarlos
     * alfabéticamente.
     * Tipo: ArbolAVL de Objetos Autor (que es Comparable)
     * Complejidad esperada: O(log n) para inserción/búsqueda.
     */
    private ArbolAVL<Autor> indiceAutores;
    private ArbolAVL<String> indiceTitulosOrdenados;

    /**
     * Índice de Palabras Clave para Búsqueda Rápida (Req. 3):
     * Permite buscar resúmenes por palabra clave en O(1).
     * Clave: Palabra Clave (String)
     * Valor: ListaSimple de Resúmenes que contienen esa palabra.
     * Complejidad esperada: O(1) para búsqueda.
     */
    private TablaHash<String, ListaSimple<Resumen>> indicePalabrasClaveHash;

    /**
     * Índice de Palabras Clave para Listado Ordenado (Req. 5):
     * Mantiene una lista única de todas las palabras clave, ordenadas
     * alfabéticamente.
     * Tipo: ArbolAVL de Strings.
     * Complejidad esperada: O(log n) para inserción.
     */
    private ArbolAVL<String> indicePalabrasClaveAVL;

    /**
     * Constructor del Gestor.
     * Inicializa todas las estructuras de datos.
     */
    public GestorInvestigaciones() {
        // Inicializamos las estructuras.
        // Elegimos un tamaño (preferiblemente primo) para las TablaHash.
        this.repositorioResumenes = new TablaHash<>(101); // Tamaño inicial de 101
        this.indicePalabrasClaveHash = new TablaHash<>(251); // Tamaño mayor para palabras
        
        this.indiceAutores = new ArbolAVL<>();
        this.indicePalabrasClaveAVL = new ArbolAVL<>();
        this.indiceTitulosOrdenados = new ArbolAVL<>();

        // (Opcional) Aquí podrías llamar a un método para precargar datos
        // como lo pide el Requerimiento Técnico 6.
        precargarDatos(); 
    }
    
    public void guardarDatos() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("datos_supermetro.ser"))) {
            out.writeObject(this); // Se guarda a sí mismo y a todas sus estructuras internas
            System.out.println("Datos guardados correctamente en 'datos_supermetro.ser'");
        } catch (IOException e) {
            System.err.println("Error al guardar los datos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * REQUERIMIENTO 6: Cargar los datos desde el disco.
     * Intenta leer el archivo y devuelve el objeto Gestor guardado.
     * Si falla o no existe, devuelve un Gestor nuevo y vacío.
     * * @return Un objeto GestorInvestigaciones (recuperado o nuevo).
     */
    public static GestorInvestigaciones cargarDatos() {
        File archivo = new File("datos_supermetro.ser");
        if (!archivo.exists()) {
            System.out.println("No se encontraron datos guardados. Iniciando sistema vacío.");
            return new GestorInvestigaciones(); // Retorna uno nuevo
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(archivo))) {
            Object objeto = in.readObject();
            System.out.println("Datos cargados exitosamente.");
            return (GestorInvestigaciones) objeto; // Casteamos y devolvemos
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al cargar los datos: " + e.getMessage());
            return new GestorInvestigaciones(); // Si falla, devolvemos uno nuevo por seguridad
        }
    }
    
    /**
     * Elimina un resumen específico por su título.
     * Elimina referencias en autores, palabras clave y árboles.
     */
    public boolean eliminarResumen(String titulo) {
        // 1. Buscar el resumen antes de borrarlo
        Resumen resumen = this.repositorioResumenes.buscar(titulo);
        if (resumen == null) return false;

        // 2. Eliminar de la Tabla Hash Principal
        this.repositorioResumenes.eliminar(titulo);

        // 3. Eliminar del Árbol de Títulos Ordenados
        this.indiceTitulosOrdenados.eliminar(titulo);

        // 4. Eliminar referencia de los Autores
        ListaSimple<String> autores = resumen.getAutores();
        for (int i = 0; i < autores.getTamano(); i++) {
            // Buscamos al autor
            Autor autor = this.indiceAutores.buscar(new Autor(autores.get(i)));
            if (autor != null) {
                // Eliminamos el resumen de su lista personal
                autor.getResumenes().eliminar(resumen);
            }
        }

        // 5. Eliminar referencia de las Palabras Clave
        ListaSimple<String> palabras = resumen.getPalabrasClave();
        for (int i = 0; i < palabras.getTamano(); i++) {
            String palabra = palabras.get(i);
            ListaSimple<Resumen> lista = this.indicePalabrasClaveHash.buscar(palabra);
            if (lista != null) {
                lista.eliminar(resumen);
            }
        }

        return true;
    }

    // --- 2. MÉTODOS PRINCIPALES (LLAMADOS POR LA GUI) ---

    /**
     * REQUERIMIENTO 1: Agregar un nuevo resumen al sistema.
     * Este método actualiza todas las estructuras de datos.
     *
     * @param nuevoResumen El objeto Resumen a agregar.
     * @return true si se agregó con éxito, false si el título ya existía.
     */
    public boolean agregarResumen(Resumen nuevoResumen) {
        
        // 1. Intentar insertar en el repositorio principal
        boolean exito = this.repositorioResumenes.insertar(nuevoResumen.getTitulo(), nuevoResumen);

        if (!exito) {
            // El resumen (por título) ya existe.
            return false;
        }
        
        this.indiceTitulosOrdenados.insertar(nuevoResumen.getTitulo());

        // 2. Procesar y agregar Autores
        ListaSimple<String> nombresAutores = nuevoResumen.getAutores();
        for (int i = 0; i < nombresAutores.getTamano(); i++) {
            String nombreAutor = nombresAutores.get(i);
            
            // Creamos un objeto Autor temporal solo para buscar
            // (El constructor ahora hace trim() automáticamente)
            Autor autorBusqueda = new Autor(nombreAutor); 
            
            // Buscar el autor en el AVL
            // (El ArbolAVL usará el compareToIgnoreCase nuevo que hicimos)
            Autor autorExistente = this.indiceAutores.buscar(autorBusqueda);
            
            if (autorExistente == null) {
                // CASO 1: El autor NO existe en el árbol.
                // Creamos uno nuevo, le asignamos el resumen y lo guardamos.
                Autor autorNuevo = new Autor(nombreAutor);
                autorNuevo.agregarResumen(nuevoResumen);
                this.indiceAutores.insertar(autorNuevo);
            } else {
                // CASO 2: El autor YA existe (ej. encontramos "Felipe").
                // NO creamos uno nuevo. Usamos el existente y le añadimos el resumen.
                autorExistente.agregarResumen(nuevoResumen);
            }
        }

        // 3. Procesar y agregar Palabras Clave
        ListaSimple<String> palabras = nuevoResumen.getPalabrasClave();
        for (int i = 0; i < palabras.getTamano(); i++) {
            String palabra = palabras.get(i);

            // a. Agregar al AVL de listado ordenado (él maneja duplicados)
            this.indicePalabrasClaveAVL.insertar(palabra);

            // b. Agregar al Hash de búsqueda O(1)
            ListaSimple<Resumen> listaResumenes = this.indicePalabrasClaveHash.buscar(palabra);
            if (listaResumenes == null) {
                // Si la palabra es nueva, creamos una lista y la agregamos
                ListaSimple<Resumen> nuevaLista = new ListaSimple<>();
                nuevaLista.agregar(nuevoResumen);
                this.indicePalabrasClaveHash.insertar(palabra, nuevaLista);
            } else {
                // Si la palabra ya existe, añadimos el resumen a su lista
                listaResumenes.agregar(nuevoResumen);
            }
        }
        
        return true; // Éxito
    }

    /**
     * REQUERIMIENTO 2.b: Buscar un resumen por su título.
     *
     * @param titulo El título a buscar.
     * @return El objeto Resumen, o null si no se encuentra.
     */
    public Resumen buscarResumenPorTitulo(String titulo) {
        // La TablaHash hace el trabajo pesado en O(1)
        return this.repositorioResumenes.buscar(titulo);
    }
    
    /**
     * REQUERIMIENTO 3: Buscar Investigaciones por palabra clave.
     *
     * @param palabra La palabra clave a buscar.
     * @return Una ListaSimple de Resúmenes, o null si la palabra no existe.
     */
    public ListaSimple<Resumen> buscarResumenPorPalabra(String palabra) {
        // La TablaHash de palabras clave hace la búsqueda en O(1)
        return this.indicePalabrasClaveHash.buscar(palabra);
    }

    /**
     * REQUERIMIENTO 4: Listar autores ordenados alfabéticamente.
     *
     * @return Una ListaSimple de objetos Autor, ordenados.
     */
    public ListaSimple<Autor> getAutoresOrdenados() {
        // El ArbolAVL hace el recorrido InOrden en O(n)
        return this.indiceAutores.getListaInorden();
    }
    
    /**
     * REQUERIMIENTO 5: Listar palabras clave ordenadas alfabéticamente.
     *
     * @return Una ListaSimple de Strings (palabras), ordenadas.
     */
    public ListaSimple<String> getPalabrasClaveOrdenadas() {
        // El ArbolAVL hace el recorrido InOrden en O(n)
        return this.indicePalabrasClaveAVL.getListaInorden();
    }
    
    public ListaSimple<String> getTitulosOrdenados() {
        // El ArbolAVL hace el recorrido InOrden en O(n)
        return this.indiceTitulosOrdenados.getListaInorden(); 
    }
    /**
     * REQUERIMIENTO 6: Guardar y Cargar Datos (Serialización).
     * (Estos métodos son más complejos y se dejan para el final).
     */
    
    private void precargarDatos() {
        // --- RESUMEN 1 ---
        ListaSimple<String> autores1 = new ListaSimple<>();
        autores1.agregar("Alan Turing");
        autores1.agregar("Ada Lovelace");

        ListaSimple<String> palabras1 = new ListaSimple<>();
        palabras1.agregar("computación");
        palabras1.agregar("algoritmos");
        palabras1.agregar("historia");

        String cuerpo1 = "Este trabajo explora los fundamentos de la computación moderna y los algoritmos. "
                + "Se discute la importancia de la máquina de Turing y el primer algoritmo diseñado por Lovelace. "
                + "La computación ha evolucionado drásticamente desde entonces.";

        Resumen r1 = new Resumen("Orígenes de la Computación", autores1, cuerpo1, palabras1);
        
        // Lo agregamos al sistema
        this.agregarResumen(r1);


        // --- RESUMEN 2 ---
        ListaSimple<String> autores2 = new ListaSimple<>();
        autores2.agregar("John McCarthy");
        autores2.agregar("Marvin Minsky");

        ListaSimple<String> palabras2 = new ListaSimple<>();
        palabras2.agregar("inteligencia artificial");
        palabras2.agregar("redes neuronales");
        palabras2.agregar("futuro");

        String cuerpo2 = "La inteligencia artificial (IA) busca simular la inteligencia humana en máquinas. "
                + "Desde las primeras redes neuronales hasta los modelos de lenguaje actuales, el futuro de la IA es prometedor "
                + "y plantea desafíos éticos importantes.";

        Resumen r2 = new Resumen("Introducción a la Inteligencia Artificial", autores2, cuerpo2, palabras2);

        // Lo agregamos al sistema
        this.agregarResumen(r2);
    }
    

    
}
