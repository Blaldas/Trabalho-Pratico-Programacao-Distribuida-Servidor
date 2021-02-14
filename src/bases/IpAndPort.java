package bases;

import conecao.Conteudo;

import java.io.Serializable;
import java.net.InetSocketAddress;

/*
Esta classe guarda IPs e portos
E suposto ser usada em duas situações:
Cliente-> guarda numa lista os ips e portos dos outros serviores conhecidos<-this is lixo
Servidor-> guarda num lista os ips e portos de clientes conhecidos
        -> junto com a classe Utilizador, guarda uma lista de utilizadores conhecidos
                                        Se, nessa lista, esta classe estiver a null é porque o cliente não esta online
*/
public class IpAndPort extends Conteudo implements Serializable {

    static final long serialVersionUID = 42L;
    InetSocketAddress conctionData;
    boolean isOnline;

    //contrutor, recebe ip e porta
    public IpAndPort( String host, int port){

         conctionData = new InetSocketAddress(host, port);
         isOnline = true;
    }

    //serve para indicar se esta online ou nao
    public boolean isOnline(){
        return isOnline();
    }

    //muda o boolan qu indica se esta onlien
    //nao devolve nada
    // NOTA:por uma questao de precaução aconselha-se a utilizar o "isOline()" após chamar esta funcao
    public void changeOnlineStatus(){
        isOnline = !isOnline;
    }

    //faz update para um novo poto e ip
    public void updateIpAndPort( String host,int port){
        conctionData = new InetSocketAddress(host, port);
    }

    public InetSocketAddress getConctionData() {
        return conctionData;
    }




}
