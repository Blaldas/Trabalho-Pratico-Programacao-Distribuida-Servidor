package bases;

import conecao.Conteudo;

import java.io.Serializable;
import java.util.List;

public class ListaMensagensResposta extends Conteudo implements Serializable {
    static final long serialVersionUID = 42L;
    private List<Mensagem> listaMensagens;


    public ListaMensagensResposta(List<Mensagem> listaMensagens) {
        this.listaMensagens = listaMensagens;
    }

    public List<Mensagem> getListaMensagens() {
        return listaMensagens;
    }
}

