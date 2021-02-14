package conecaoMultisocketUPD;

import java.io.Serializable;

/*Explicação
* Esta classe é a classe enviada entre as varias instancias do programa
* Ela tem um código de mensagem e um obj
* Esse obj será transformado nas devidas classes conforme o código  recebido
*/
public class ConecaoMultisocketUDP implements Serializable {
    static final long serialVersionUID = 42L;
    private String msgCode;
    private Object obj;
    int serialNumber;           //numero de serie para mensagens compridas.

    public ConecaoMultisocketUDP(String msgCode, Object o, int serialNumber){
        this.msgCode = msgCode;
        this.obj = o;
        this.serialNumber = serialNumber;
    }


    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public String getMsgCode() {
        return msgCode;
    }

    public void setMsgCode(String msgCode) {
        this.msgCode = msgCode;
    }
}
