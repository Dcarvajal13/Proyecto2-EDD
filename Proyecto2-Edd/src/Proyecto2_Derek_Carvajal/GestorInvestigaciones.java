/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Proyecto2_Derek_Carvajal;

/**
 *
 * @author derek
 */


import java.io.*;
import java.io.Serializable;

public class GestorInvestigaciones implements Serializable{

    /*
     * 
     * Almacena todos los Resúmenes.
     * Clave: Título del Resumen (String)
     * Valor: Objeto Resumen
     */
    private TablaHash<String, Resumen> repositorioResumenes;

    /*
     * Almacena un objeto Autor único por cada autor para listarlos
     * alfabéticamente.
     * Tipo: ArbolAVL de Objetos Autor (que es Comparable)
     */
    private ArbolAVL<Autor> indiceAutores;
    
    private ArbolAVL<String> indiceTitulosOrdenados;
    
    /**
     * Permite buscar resúmenes por palabra clave en O(1).
     * Clave: Palabra Clave (String)
     * Valor: ListaSimple de Resúmenes que contienen esa palabra.
     * Complejidad esperada: O(1) para búsqueda.
     */
    private TablaHash<String, ListaSimple<Resumen>> indicePalabrasClaveHash;

    /*
     * Mantiene una lista única de todas las palabras clave, ordenadas
     * alfabéticamente.
     * Tipo: ArbolAVL de Strings.
     * Complejidad esperada: O(log n) para inserción.
     */
    private ArbolAVL<String> indicePalabrasClaveAVL;

    
    /** Constructor
     * 
     */
    public GestorInvestigaciones() {
        // Inicializo las estructuras
        // Elijo un tamaño  para las TablaHash.
        this.repositorioResumenes = new TablaHash<>(101); // Tamaño inicial de 101
        this.indicePalabrasClaveHash = new TablaHash<>(50); // Tamaño mayor para palabras
        
        this.indiceAutores = new ArbolAVL<>();
        this.indicePalabrasClaveAVL = new ArbolAVL<>();
        this.indiceTitulosOrdenados = new ArbolAVL<>();

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

    /*
     * Intenta leer el archivo y devuelve el objeto Gestor guardado.
     * Si falla o no existe, devuelve un Gestor nuevo y vacío.
     * * @return Un objeto GestorInvestigaciones
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
        //  Buscar el resumen antes de borrarlo
        Resumen resumen = this.repositorioResumenes.buscar(titulo);
        if (resumen == null) return false;

        // Eliminar de la Tabla Hash Principal
        this.repositorioResumenes.eliminar(titulo);

        // Eliminar del Árbol de Títulos Ordenados
        this.indiceTitulosOrdenados.eliminar(titulo);

        // Eliminar referencia de los Autores
        ListaSimple<String> autores = resumen.getAutores();
        for (int i = 0; i < autores.getTamano(); i++) {
            // Buscamos al autor
            Autor autor = this.indiceAutores.buscar(new Autor(autores.get(i)));
            if (autor != null) {
                // Eliminamos el resumen de su lista personal
                autor.getResumenes().eliminar(resumen);
            }
        }

        // Eliminar referencia de las Palabras Clave
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

   

    /**
     * Agregar un nuevo resumen al sistema
     * Este método actualiza todas las estructuras de datos
     *
     * @param nuevoResumen El objeto Resumen a agregar
     * @return true si se agregó con éxito, false si el título ya existía
     */
    public boolean agregarResumen(Resumen nuevoResumen) {
        
        //Intentar insertar en el repositorio principal
        boolean exito = this.repositorioResumenes.insertar(nuevoResumen.getTitulo(), nuevoResumen);

        if (!exito) {
            // El resumen (por título) ya existe.
            return false;
        }
        
        this.indiceTitulosOrdenados.insertar(nuevoResumen.getTitulo());

        // Procesar y agregar Autores
        ListaSimple<String> nombresAutores = nuevoResumen.getAutores();
        for (int i = 0; i < nombresAutores.getTamano(); i++) {
            String nombreAutor = nombresAutores.get(i);
            
            // Creo un objeto Autor temporal solo para buscar
            // (El constructor ahora hace trim() automáticamente)
            Autor autorBusqueda = new Autor(nombreAutor); 
            
            // Buscar el autor en el AVL
            // (El ArbolAVL usará el compareToIgnoreCase nuevo que hice)
            Autor autorExistente = this.indiceAutores.buscar(autorBusqueda);
            
            if (autorExistente == null) {
                // CASO 1: El autor NO existe en el árbol.
                // Se crea uno nuevo, le asigno el resumen y lo guardo
                
                Autor autorNuevo = new Autor(nombreAutor);
                autorNuevo.agregarResumen(nuevoResumen);
                this.indiceAutores.insertar(autorNuevo);
            } else {
                // CASO 2: El autor YA existe
                // No creo uno nuevo. Uso el existente y le añado el resumen
                
                autorExistente.agregarResumen(nuevoResumen);
            }
        }

        // Procesar y agregar Palabras Clave
        ListaSimple<String> palabras = nuevoResumen.getPalabrasClave();
        for (int i = 0; i < palabras.getTamano(); i++) {
            String palabra = palabras.get(i);

            // Agregar al AVL de listado ordenado
            this.indicePalabrasClaveAVL.insertar(palabra);

            // Agregar al Hash de búsqueda O(1)
            ListaSimple<Resumen> listaResumenes = this.indicePalabrasClaveHash.buscar(palabra);
            if (listaResumenes == null) {
                // Si la palabra es nueva, creo una lista y la agrego
                ListaSimple<Resumen> nuevaLista = new ListaSimple<>();
                nuevaLista.agregar(nuevoResumen);
                this.indicePalabrasClaveHash.insertar(palabra, nuevaLista);
            } else {
                // Si la palabra ya existe, añado el resumen a su lista
                listaResumenes.agregar(nuevoResumen);
            }
        }
        
        return true; 
    }
    
    public ListaSimple<Resumen> buscarResumenesPorAutor(String nombreAutor) {
     
        Autor autorBusqueda = new Autor(nombreAutor);
        
        // Busco en el Árbol AVL
        Autor autorEncontrado = indiceAutores.buscar(autorBusqueda);
        
        // Si existe, devuelvo sus resúmenes
        if (autorEncontrado != null) {
            return autorEncontrado.getResumenes();
        } else {
            return null; // No se encontró el autor
        }
    }

    /**
     * Buscar un resumen por su título
     *
     * @param titulo El título a buscar
     * @return El objeto Resumen, o null si no se encuentra
     */
    public Resumen buscarResumenPorTitulo(String titulo) {
        // La TablaHash hace el trabajo pesado en O(1)
        return this.repositorioResumenes.buscar(titulo);
    }
    
    /**
     * Buscar Investigaciones por palabra clave
     *
     * @param palabra La palabra clave a buscar
     * @return Una ListaSimple de Resúmenes, o null si la palabra no existe
     */
    public ListaSimple<Resumen> buscarResumenPorPalabra(String palabra) {
        // La TablaHash de palabras clave hace la búsqueda en O(1)
        return this.indicePalabrasClaveHash.buscar(palabra);
    }

    /**
     *Listar autores ordenados alfabéticamente
     *
     * @return Una ListaSimple de objetos Autor, ordenados
     */
    public ListaSimple<Autor> getAutoresOrdenados() {
        // El ArbolAVL hace el recorrido InOrden en O(n)
        return this.indiceAutores.getListaInorden();
    }
    
    /**
     * Listar palabras clave ordenadas alfabéticamente.
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
     * Guardar y Cargar Datos (Serialización).
     * 
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
        
        // Lo agrego al sistema
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

        // Lo agrego al sistema
        this.agregarResumen(r2);
    }
    
    public ListaSimple<String> obtenerTitulosDisponibles() {
        return repositorioResumenes.obtenerTodasLasClaves();
    }

    // Lógica completa para el botón ELIMINAR 
    public boolean eliminarResumenGlobal(String titulo) {
        Resumen aBorrar = repositorioResumenes.buscar(titulo);
        if (aBorrar == null) return false;

        // Borrar del repositorio principal
        boolean borradoHash = repositorioResumenes.eliminar(titulo);

        // Borrar referencias en Autores 
        ListaSimple<String> autores = aBorrar.getAutores();
        for(int i=0; i<autores.getTamano(); i++) {
            Autor autorObj = indiceAutores.buscar(new Autor(autores.get(i)));
            if(autorObj != null) {
                
                autorObj.getResumenes().eliminar(aBorrar);
            }
        }
        
        return borradoHash;
    }

    // Lógica para el botón ANALIZAR
    public String obtenerDetallesResumen(String titulo) {
        Resumen r = repositorioResumenes.buscar(titulo);
        if (r == null) return "Error: No se encontró el resumen.";
        
        return "TÍTULO: " + r.getTitulo() + "\n\n" +
               "AUTORES: " + mostrarLista(r.getAutores()) + "\n" +
               "RESUMEN: \n" + r.getCuerpoResumen() + "\n\n" +
               "PALABRAS CLAVE: " + mostrarLista(r.getPalabrasClave());
    }

    // Auxiliar para convertir lista a texto
    private String mostrarLista(ListaSimple<String> lista) {
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<lista.getTamano(); i++) {
            sb.append(lista.get(i)).append(", ");
        }
        return sb.toString();
    }
    public String generarListadoPalabras() {
        // Verifico si la tabla de palabras existe
        if (indicePalabrasClaveHash == null) {
            return "Error: El índice de palabras clave no ha sido inicializado.";
        }

        ListaSimple<String> listaPalabras = indicePalabrasClaveHash.obtenerTodasLasClaves();
        
        if (listaPalabras.getTamano() == 0) {
            return "No hay palabras clave registradas en el sistema.";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("=== PALABRAS CLAVE REGISTRADAS ===\n\n");
        
        // Recorro la lista para armar el texto
        for (int i = 0; i < listaPalabras.getTamano(); i++) {
            sb.append("• ").append(listaPalabras.get(i)).append("\n");
        }
        
        return sb.toString();
    }
    
     
   

    
}
