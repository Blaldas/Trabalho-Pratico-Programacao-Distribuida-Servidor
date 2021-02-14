package classesAjuda;

import bases.IpAndPort;
import conecao.Conteudo;

import java.io.Serializable;
import java.util.List;

public class ListaIpsandPorts extends Conteudo implements Serializable {
    static final long serialVersionUID = 42L;
    private List<IpAndPort> lista;

    public ListaIpsandPorts(List<IpAndPort> lista){
        this.lista = lista;
    }

    public List<IpAndPort> getLista() {
        return lista;
    }

    public void setLista(List<IpAndPort> lista) {
        this.lista = lista;
    }
}
