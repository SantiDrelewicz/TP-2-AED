package aed;

public class Ciudad {

    public int ganancia;
    public int perdida;
    private int numero;
    public int indiceSuperavit;
     

    public Ciudad (int numero){
        ganancia = 0; 
        perdida = 0;
        this.numero = numero; 
        indiceSuperavit = -1;
    }

    public int superavit(){
        return ganancia - perdida;
    }

    public int numero(){
        return numero; 
    }

}

