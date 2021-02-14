package classesAjuda;

import java.io.Serializable;

public class ServidoresConectados implements Comparable<ServidoresConectados>, Serializable {

    private String ip;
    private int numeroUtilizadoresLigados;

    public ServidoresConectados(String ip, int numeroUtilizadoresLigados) {
        this.ip = ip;
        this.numeroUtilizadoresLigados = numeroUtilizadoresLigados;
    }


    public int getNumeroUtilizadoresLigados() {
        return numeroUtilizadoresLigados;
    }

    public void setNumeroUtilizadoresLigados(int numeroUtilizadoresLigados) {
        this.numeroUtilizadoresLigados = numeroUtilizadoresLigados;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public int compareTo(ServidoresConectados o) {
        return numeroUtilizadoresLigados;
    }
}
