package aed;

import java.util.ArrayList;
import java.util.Comparator;

public class BestEffort {

    private Heap<Traslado> trasladosPorGanancia;
    private Heap<Traslado> trasladosPorAntiguedad;
    private int cantTrasladosDespachados;
    private int gananciaNetaTotal;
    private Ciudad[] infoCiudades;
    private Heap<Ciudad> superavit;
    private ArrayList<Integer> ciudadesMaxGanancia;
    private ArrayList<Integer> ciudadesMaxPerdida;


    private void actualizarIndicesEnGanancia(ArrayList<Integer> listaIndices){    // 
        for (int i : listaIndices){ // |listaIndices| operaciones O(1)
            trasladosPorGanancia.obtener(i).indiceGanancia = i; // O(1)
        }
    }

    private void actualizarIndicesEnAntiguedad(ArrayList<Integer> listaIndices){  // Complejidad: O(|listaIndices|)
        for (int i : listaIndices){
            trasladosPorAntiguedad.obtener(i).indiceAntiguedad = i;
        }
    }

    private void actualizarIndicesEnSuperavit(ArrayList<Integer> listaIndices){   //
        for (int i : listaIndices){
            superavit.obtener(i).indiceSuperavit = i;
        }
    }

    public BestEffort(int cantCiudades, Traslado[] traslados){ // Complejidad: O(T + C)
        // inicializamos los heap vacios
        trasladosPorGanancia = new Heap<>(Comparator.comparing(Traslado::gananciaNeta).thenComparing(Traslado::id, Comparator.reverseOrder())); // O(1)
        trasladosPorAntiguedad = new Heap<>(Comparator.comparing(Traslado::timestamp).reversed());                                              // O(1)
        //Le agregamos los traslados a los heaps y los ordenamos 
        trasladosPorGanancia = trasladosPorGanancia.heapDesdeSecuencia(traslados);      // O(T)
        trasladosPorAntiguedad = trasladosPorAntiguedad.heapDesdeSecuencia(traslados);  // O(T)
        // seteamos las posiciones iniciales de los traslados en cada heap
        for (int i = 0; i < traslados.length; i++){ // O(|T)
            trasladosPorGanancia.obtener(i).indiceGanancia = i;     // O(1) 
            trasladosPorAntiguedad.obtener(i).indiceAntiguedad = i; // O(1)
        }
        // Creamos un array de ciudades donde cada posicion representa el numero de ciudad y le inicializamos la ganancia y la perdida en 0 
        infoCiudades = new Ciudad[cantCiudades];    // O(1)  
        for (int num = 0; num < cantCiudades; num++){   // O(C)
            infoCiudades[num] = new Ciudad(num);    // O(1)
        }
        // Inicialiazmos el heap de superavit y lo ordenamoS
        superavit = new Heap<>(Comparator.comparing(Ciudad::superavit).thenComparing(Ciudad::numero, Comparator.reverseOrder()));
        superavit = superavit.heapDesdeSecuencia(infoCiudades);
        // seteamos las posiciones iniciales de los ciudades en el heap superavit
        for (int i = 0; i < cantCiudades; i++){ // O(C)
            superavit.obtener(i).indiceSuperavit = i;   // O(1)
        }
        cantTrasladosDespachados = 0;   // O(1)
        gananciaNetaTotal = 0;          // O(1)
        
        // creamos dos arrays vacios los cuales van a contener las ciudades con mayor ganancia y perdidaa
        ciudadesMaxGanancia = new ArrayList<Integer>(); // O(1)
        ciudadesMaxPerdida = new ArrayList<Integer>();  // O(1) 

    }

    public void registrarTraslados(Traslado[] traslados){   // Complejidad: O(|traslados|log T)
        // Agregamos c/traslados a c/heap y vamos actualizando los indices donde quedan en c/u
        for (Traslado traslado : traslados){    // |traslado| # iteraciones de operaciones O(log T)
            // Cuando agreguemos un traslado al heap a lo sumo cambiarán O(altura) indices
            actualizarIndicesEnGanancia(trasladosPorGanancia.agregar(traslado));        // O(log T)
            actualizarIndicesEnAntiguedad(trasladosPorAntiguedad.agregar(traslado));    // O(log T)
        }
    }

    private void actualizarBalanceOrigenDestino(Traslado traslado){ // Complejidad: O(1)
        infoCiudades[traslado.origen()].ganancia += traslado.gananciaNeta();    // O(1)
        infoCiudades[traslado.destino()].perdida += traslado.gananciaNeta();    // O(1)
    }

    private void actualizarCiudadesConMayorGanancia(Traslado traslado){ // Complejidad: O(1)
    // Al derpachar un traslado hacemo lo siguiente: 
        int numeroDeCiudadOrigen = infoCiudades[traslado.origen()].numero();
        int gananciaTotalOrigen = infoCiudades[traslado.origen()].ganancia + traslado.gananciaNeta();
        // - si el array de max ganancias esta vacio o la gananciaTotal actualizada de origen es igual a las ciudades que ya estaban agregamos el numero de la ciudad origen
        // - si la gananciaTotal de origen es mayor que las de las que estaban en max ganancias limpiamos el array y agregamos a origen
        if (ciudadesMaxGanancia.size() == 0 || gananciaTotalOrigen == infoCiudades[ciudadesMaxGanancia.get(0)].ganancia)    // O(1)
            ciudadesMaxGanancia.add(numeroDeCiudadOrigen);                                                                  //
        else if (gananciaTotalOrigen > infoCiudades[ciudadesMaxGanancia.get(0)].ganancia){                                  // 
            ciudadesMaxGanancia.clear();                                                                                    // O(1) por enunciado
            ciudadesMaxGanancia.add(numeroDeCiudadOrigen);                                                                  // O(1)
        } else return;
    }
    
    private void actualizarCiudadesConMayorPerdida(Traslado traslado){  // Complejidad: O(1)
        // Hacemos lo mismo que en actualizarCiudadesConMayorGanancia pero con la perdidaY ciudad destino.
        int numeroDeCiudadDestino = infoCiudades[traslado.destino()].numero();
        int perdidaTotalDestino = infoCiudades[traslado.destino()].perdida + traslado.gananciaNeta();
        if (ciudadesMaxPerdida.size() == 0 || perdidaTotalDestino == infoCiudades[ciudadesMaxPerdida.get(0)].perdida)
            ciudadesMaxPerdida.add(numeroDeCiudadDestino);
        else if (perdidaTotalDestino > infoCiudades[ciudadesMaxPerdida.get(0)].perdida){
            ciudadesMaxPerdida.clear();
            ciudadesMaxPerdida.add(numeroDeCiudadDestino);
        } else return;
    }

    public int[] despacharMasRedituables(int n){    // Complejidad: O(n(log T + log C))

    // IDEA: mientras haya traslados por despaachar hacemos lo siguiente:
    //        - vemos cual es el proximo traslado a despachar y guardamos su id en la un array res
    //        - eliminamos el primer elemento del heap por ganancias y el elemento correspondiente en el heap de antiguedad
    //        - Como la funcion eliminar ya ordena los dos heaps y devuelve los indices que se modificaron en c/u  
    //        utilizamos esas listas para actualizar los indices de los traslados a despachar en c/heap
    //        - Actualizamos la ganacia y perdida de las ciudades involucradas y si corresponde lo agregamos al array de       
    //          ciudadesMaxGanancia
    // Finalmente devolvemos el array que creamos al inicio.

        int[] res = new int[n];
        for (int i = 0; i < n && !trasladosPorGanancia.vacio(); i++){   // n iteraciones de operaciones O(log T + log C) -> O(n(log T + log C))
            Traslado trasladoDespachado = trasladosPorGanancia.proximo();   // O(1)
            
            actualizarIndicesEnGanancia( // O(log T): a lo sumo cambiarán O(altura) posiciones del heap de ganancia. Esto vale para actualizarIndicesEnAntiguedad/Superavit
                trasladosPorGanancia.eliminar(0)    // O(log T)
            ); // -> O(log T)            
            actualizarIndicesEnAntiguedad(trasladosPorAntiguedad.eliminar(trasladoDespachado.indiceAntiguedad));    // O(log T)
            
            cantTrasladosDespachados ++;                            // O(1)
            gananciaNetaTotal += trasladoDespachado.gananciaNeta(); // O(1)

            actualizarCiudadesConMayorGanancia(trasladoDespachado); //
            actualizarCiudadesConMayorPerdida(trasladoDespachado);  // O(1)
            actualizarBalanceOrigenDestino(trasladoDespachado);     //

            actualizarIndicesEnSuperavit( // O(log C): 
                superavit.modificarPosicion(    // O(log C)
                    infoCiudades[trasladoDespachado.destino()].indiceSuperavit, // O(1)
                    infoCiudades[trasladoDespachado.destino()]                  // O(1)
                )
            ); // -> O(log C)

            actualizarIndicesEnSuperavit(   // O(log C)
                superavit.modificarPosicion(    // O(log C)
                    infoCiudades[trasladoDespachado.origen()].indiceSuperavit,  // O(1)
                    infoCiudades[trasladoDespachado.origen()]                   // O(1)
                )
            ); // -> O(log C)

            res[i] = trasladoDespachado.id();   // O(1)
        }
        return res;
    }

    public int[] despacharMasAntiguos(int n){   // Complejidad: O(n(log T + log C)) -> (misma justificación que en despacharMasRedituables)
        int[] res = new int[n];
        for (int i = 0; i < n && !trasladosPorAntiguedad.vacio(); i++){
            Traslado trasladoDespachado = trasladosPorAntiguedad.proximo();
            
            actualizarIndicesEnAntiguedad(trasladosPorAntiguedad.eliminar(0));
            actualizarIndicesEnGanancia(trasladosPorGanancia.eliminar(trasladoDespachado.indiceGanancia));

            cantTrasladosDespachados ++;
            gananciaNetaTotal += trasladoDespachado.gananciaNeta();

            actualizarCiudadesConMayorGanancia(trasladoDespachado);
            actualizarCiudadesConMayorPerdida(trasladoDespachado);
            actualizarBalanceOrigenDestino(trasladoDespachado);

            actualizarIndicesEnSuperavit(superavit.modificarPosicion(
                infoCiudades[trasladoDespachado.destino()].indiceSuperavit, 
                infoCiudades[trasladoDespachado.destino()]
                )
            );

            actualizarIndicesEnSuperavit(superavit.modificarPosicion(
                infoCiudades[trasladoDespachado.origen()].indiceSuperavit, 
                infoCiudades[trasladoDespachado.origen()]
                )
            );

            res[i] = trasladoDespachado.id();
        }
        return res;
    }

    public int ciudadConMayorSuperavit(){   // Complejidad: O(1)
        return superavit.proximo().numero();    // O(1)
    }

    public ArrayList<Integer> ciudadesConMayorGanancia(){   // Complejidad: O(1)
        return ciudadesMaxGanancia; // O(1)
    }

    public ArrayList<Integer> ciudadesConMayorPerdida(){    // Complejidad: O(1)
        return ciudadesMaxPerdida;  // O(1)
    }

    public int gananciaPromedioPorTraslado(){   // Complejidad: O(1)
        return gananciaNetaTotal / cantTrasladosDespachados;    // O(1)
    }
    
}
