package bases;

import conecao.Conteudo;

import java.io.Serializable;

public class PedidoListaMensagensCanal extends Conteudo implements Serializable {
    static final long serialVersionUID = 42L;

    private String nomeCanal;
    private String passCanal;
    private int numeroMensagensAListar;

    public PedidoListaMensagensCanal(String nomeCanal, String passCanal, int numeroMensagensAListar) {
        this.nomeCanal = nomeCanal;
        this.passCanal = passCanal;
        this.numeroMensagensAListar = numeroMensagensAListar;
    }

    public String getNomeCanal() {
        return nomeCanal;
    }

    public String getPassCanal() {
        return passCanal;
    }

    public int getNumeroMensagensAListar() {
        return numeroMensagensAListar;
    }
}
