package aed;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import java.util.Comparator;

public class IntegerMaxHeapTest {

    private class EnterosComparator implements Comparator<Integer>{

        @Override
        public int compare(Integer a, Integer b){
            return Integer.compare(a, b);
        }
    }

    void assertSetEquals(ArrayList<Integer> s1, ArrayList<Integer> s2) {
        assertEquals(s1.size(), s2.size());
        for (int e1 : s1) {
            boolean encontrado = false;
            for (int e2 : s2) {
                if (e1 == e2) encontrado = true;
            }
            assertTrue(encontrado, "No se encontró el elemento " +  e1 + " en el arreglo " + s2.toString());
        }
    }

    @Test
    void array2Heap() {

        Integer[] seq = new Integer[]{66,5,4,67,23,64,45,21,89,68,67,39,33};

        Heap<Integer> maxHeap = new Heap<>(new EnterosComparator());

        assertTrue(maxHeap.vacio());

        maxHeap = maxHeap.heapDesdeSecuencia(seq);

        assertEquals(seq.length, maxHeap.cantElems());

        assertFalse(maxHeap.vacio());

        assertEquals("[89,68,64,67,67,39,45,21,5,23,66,4,33]", maxHeap.toStringComoArray());

    }

    @Test 
    void agregarVariosElemsYEliminarTodos() {

        Heap<Integer> maxHeap = new Heap<>(new EnterosComparator());

        int n = 100;

       for (int i = 1; i < n + 1; i++){
            maxHeap.agregar(i);
       }

       assertEquals(n, maxHeap.cantElems());

        for (int i = 100; i > 0; i--){
            assertEquals(i, maxHeap.proximo());
            maxHeap.eliminar(0);
        }

        assertTrue(maxHeap.vacio());

    }

    @Test
    void indicesModificadosCorrectos(){

        Integer[] seq = new Integer[]{7,6,5,4,3,2,1};

        Heap<Integer> maxHeap = new Heap<>(new EnterosComparator());
        maxHeap = maxHeap.heapDesdeSecuencia(seq);

        assertSetEquals(new ArrayList<>(Arrays.asList(0,1,3)), maxHeap.eliminar(0));

    }

    @Test 
    void eliminarPosicionIntermediaDelheap(){

        Integer[] seq = new Integer[]{7,6,5,4,3,2,1};

        Heap<Integer> maxHeap = new Heap<>(new EnterosComparator());
        maxHeap = maxHeap.heapDesdeSecuencia(seq);

        maxHeap.eliminar(2);

        assertEquals("[7,6,2,4,3,1]", maxHeap.toStringComoArray());

    }

    @Test
    void modificarHaciaArribaUnaPosicionDelHeap(){

        Integer[] seq = new Integer[]{7,6,5,4,3,2,1};

        Heap<Integer> maxHeap = new Heap<>(new EnterosComparator());
        maxHeap = maxHeap.heapDesdeSecuencia(seq);

        maxHeap.modificarPosicion(2, 8);

        assertEquals("[8,6,7,4,3,2,1]", maxHeap.toStringComoArray());

    }

    @Test
    void modificarHaciaAbajoUnaPosicionDelHeap(){

        Integer[] seq = new Integer[]{7,6,5,4,3,2,1};

        Heap<Integer> maxHeap = new Heap<>(new EnterosComparator());
        maxHeap = maxHeap.heapDesdeSecuencia(seq);

        maxHeap.modificarPosicion(1, 2);

        assertEquals("[7,4,5,2,3,2,1]", maxHeap.toStringComoArray());

    }

}
