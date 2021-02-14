package bases;

import conecao.Conteudo;

import java.io.Serializable;

public class ServerTime implements Serializable {

    static final long serialVersionUID = 42L;
    private int horas, minutos, segundos;


    public ServerTime(int h, int m, int s) {
        horas = h;
        minutos = m;
        segundos = s;
    }

    @Override
    public String toString() {
        return horas + ":" + minutos + ":" + segundos;
    }

    public int getHoras() {
        return horas;
    }

    public void setHoras(int horas) {
        this.horas = horas;
    }

    public int getMinutos() {
        return minutos;
    }

    public void setMinutos(int minutos) {
        this.minutos = minutos;
    }

    public int getSegundos() {
        return segundos;
    }

    public void setSegundos(int segundos) {
        this.segundos = segundos;
    }

}
