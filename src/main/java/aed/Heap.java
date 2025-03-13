package aed;

import java.util.ArrayList;
import java.util.Comparator;

public class Heap<T> {

    private ArrayList<T> datos;
    private int cantElems;
    private Comparator<T> comparator;

    public Heap(Comparator<T> comparator) { // Complejidad: O(1)
        cantElems = 0;                  // O(1)   
        datos = new ArrayList<T>();     // O(1)
        this.comparator = comparator;   // O(1)
    }

    public Heap<T> heapDesdeSecuencia(T[] arreglo) {
        cantElems = arreglo.length;
        for (T t : arreglo) { // Primero agregamos todos los elementos del arreglo a datos en O(n), con n la longitud del arreglo
            datos.add(t); // O(1) por enunciado
        }
        heapifyData(); // Luego aplicamos heapify a los datos: O(n)
        return this;
    }

    public boolean vacio() {    // Complejidad: O(1)
        return cantElems == 0;
    }

    public int cantElems() {    //Complejidad: O(1)
        return cantElems;
    }

    public T obtener(int indice){   //Complejidad: O(1)
        return datos.get(indice);   // O(1) por enunciado
    }

    public T proximo() {   // Complejidad: O(1)
        return datos.get(0);
    }
    
    public ArrayList<Integer> agregar(T t) { // Complejidad: O(log n)
        // Si la cantidad de elementos del heap es igual al largo de los datos lo tenemos que agregar atrás
        // Si el largo del array de elementos es mayor porquea antes eliminamos algún elemento, lo reemplazamos por el elemento en la posción cantElems
        if (cantElems == datos.size()) datos.add(t);    // O(1)  (si bien size() no está en el aanunciado asumimos que es O(1))
        else datos.set(cantElems, t);                   // O(1)
        cantElems ++;                                   // O(1)
        return subirIterativo(cantElems - 1);           // O(log n)
    }

    private ArrayList<Integer> subirIterativo(int indice) {  // Complejidad O(log n)
        // la funcion devuelve una lista con las posiciones que fueron modificadas dentro del heap 
        ArrayList<Integer> indicesCambiados = new ArrayList<>();    //
        indicesCambiados.add(indice);                               //
        int posicionActual = indice;                                // o(1)
        T elem = datos.get(indice);                                 //
        int indicePadre = (posicionActual - 1) / 2;                 //
        // Como la cantidad de iteraciones que realiza es como mucho la altura del arbol 
        // y la altura del arbol es log(n), con n la longitud del array  -> # max iteraciones O(log n)
        while (posicionActual > 0 && comparator.compare(elem, datos.get(indicePadre)) > 0) {
            datos.set(posicionActual, datos.get(indicePadre));  //
            datos.set(indicePadre, elem);                       //
            posicionActual = indicePadre;                       // o(1)
            indicePadre = (posicionActual - 1) / 2;             //
            indicesCambiados.add(posicionActual);               //
        }
        return indicesCambiados;      
    }

    public ArrayList<Integer> eliminar(int indice) {    // Complejidad O(log n)
        // reemplazo el elemento en la posición indice por el ultimo
        datos.set(indice, datos.get(cantElems - 1));    // O(1)
        cantElems --;                                   // O(1)
        return bajarIterativo(indice);                  //O(log n)
    }

    private ArrayList<Integer> bajarIterativo(int posicion) {   // Complejidad O(log n)
        // la funcion devuelve una lista con las posiciones que fueron modificadas dentro del heap
        ArrayList<Integer> indicesCambiados = new ArrayList<>();    // O(1)
        indicesCambiados.add(posicion);                             // O(1)
        // El peor caso es que al reemplazar la raiz del heap por el último elemento, sea el maximo o minimo y lkuego tener que bajarla hasta las hojas 
        // por ende bajar el elemento costara O(altura) = O(log n) en peor caso.
        while (tieneHijosMayores(posicion)) {
            posicion = intercambiarConHijoMayor(posicion);                      // O(1)
            indicesCambiados.add(posicion);                         // O(1)
        }
        return indicesCambiados;
    }

    private boolean tieneHijosMayores(int posicionActual) { // Complejidad: O(1) (la funcion solo tiene finitas asignaciones y comparaciones)
        int indiceHijoIzq = 2*posicionActual + 1;
        boolean tieneHijoIzq = indiceHijoIzq < cantElems;
        int indiceHijoDer = indiceHijoIzq + 1;
        boolean tieneHijoDer = indiceHijoDer < cantElems;
        return 
            (tieneHijoIzq && comparator.compare(datos.get(indiceHijoIzq), datos.get(posicionActual)) > 0) ||
            (tieneHijoDer && comparator.compare(datos.get(indiceHijoDer), datos.get(posicionActual)) > 0);   
    }

    private int intercambiarConHijoMayor(int posicion) { // O(1): solo hay finitas comparaciones y asignaciones.
        // elemento a intercambiar con sus hijos
        T elem = datos.get(posicion);   
        // aca ya asumimos que el elem en la posicion tiene un hijo mayor.
        int indiceHijoIzq = 2 * posicion + 1;
        int indiceHijoDer = 2 * posicion + 2;
        // Asumimos que el hijo izquierdo es el mayor por defecto.
        int indiceMayor = indiceHijoIzq; 
        if (indiceHijoDer < cantElems && comparator.compare(datos.get(indiceHijoDer), datos.get(indiceHijoIzq)) > 0) {
            // Si existe un hijo derecho y es mayor que el izquierdo, usamos el derecho.
            indiceMayor = indiceHijoDer;
        } // intercambiamos los valores con el mayor.
        datos.set(posicion, datos.get(indiceMayor));
        datos.set(indiceMayor, elem);
        // devolvemos el indice donde quedó el elem
        return indiceMayor; 
    }

    public ArrayList<Integer> modificarPosicion(int indice, T valor){   // Complejidad: O(log n)
        datos.set(indice, valor);                                       // O(1)
        if (tieneHijosMayores(indice)) return bajarIterativo(indice);   // O(log n)
        else return subirIterativo(indice);                             // O(log n)
    }
    
    private void heapifyData() { // Complejidad: O(n) por teoría
        int i = cantElems - 1;
        while (i >= 0) {
            int posicion = i;
            bajarIterativo(posicion);
            i --;                
        }
    }

    public String toStringComoArray() {
        String res = "[";
        for (int i = 0; i < cantElems; i ++) {
            res += datos.get(i).toString();
            if (i != cantElems - 1) res += ",";
        }
        return res + "]";
    }

    public static void main(String[] args) {
        Comparator<Integer> comp = (a, b) -> a - b;
        Heap<Integer> heap = new Heap<>(comp);
        Integer[] arreglo = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        heap.heapDesdeSecuencia(arreglo);
        System.out.println(heap.toStringComoArray());
        heap.agregar(10);
        System.out.println(heap.toStringComoArray());
        heap.eliminar(0);
        System.out.println(heap.toStringComoArray());
        heap.modificarPosicion(0, 0);
        System.out.println(heap.toStringComoArray());
    }

}

