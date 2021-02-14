package bases;

import conecao.Conteudo;

import java.io.Serializable;

public class PedidoListaMensagensDiretas extends Conteudo implements Serializable {
    static final long serialVersionUID = 42L;
    private String fonte;       //nome do  outro user/nome do canal
    private int listNumber;     //numero de mensagens a listar


    public PedidoListaMensagensDiretas(String fonte, int listNumber) {
        this.fonte = fonte;
        this.listNumber = listNumber;
    }

    public String getFonte() {
        return fonte;
    }

    public void setFonte(String fonte) {
        this.fonte = fonte;
    }

    public int getListNumber() {
        return listNumber;
    }

    public void setListNumber(int listNumber) {
        this.listNumber = listNumber;
    }


}
