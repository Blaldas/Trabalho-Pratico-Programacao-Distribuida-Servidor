package bases;

import conecao.Conteudo;

import java.lang.reflect.Array;
import java.util.List;

public class ChannelsAndUsersList extends Conteudo {
    static final long serialVersionUID = 42L;

    private List<String> listaCanais;
    private List<String> listaUtilizadores;


    public ChannelsAndUsersList(List<String> listaCanais, List<String> listaUtilizadores) {
        this.listaCanais = listaCanais;
        this.listaUtilizadores = listaUtilizadores;
    }

    public List<String> getListaCanais() {
        return listaCanais;
    }

    public void setListaCanais(List<String> listaCanais) {
        this.listaCanais = listaCanais;
    }

    public List<String> getListaUtilizadores() {
        return listaUtilizadores;
    }

    public void setListaUtilizadores(List<String> listaUtilizadores) {
        this.listaUtilizadores = listaUtilizadores;
    }
}
