package conecao;

import java.io.Serializable;

/*Explicação
* Esta classe é a classe enviada entre as varias instancias do programa
* Ela tem um código de mensagem e um obj
* Esse obj será transformado nas devidas classes conforme o código  recebido
*/
public class Conecao implements Serializable {
    static final long serialVersionUID = 42L;
    private String msgCode;
    private Conteudo obj;

    public Conecao(String msgCode, Conteudo o){
        this.msgCode = msgCode;
        this.obj = o;
    }


    public Object getObj() {
        return obj;
    }

    public void setObj(Conteudo obj) {
        this.obj = obj;
    }

    public String getMsgCode() {
        return msgCode;
    }

    public void setMsgCode(String msgCode) {
        this.msgCode = msgCode;
    }
}
