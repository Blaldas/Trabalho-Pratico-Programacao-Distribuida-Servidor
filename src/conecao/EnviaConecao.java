package conecao;


import typos.Typos;

import java.io.*;

//esta classe é chamada sempre que se quer enviar alguma coisa por tcp
//a classe recebe UM OBJETO Conecao, serializa-o e envia para o seu devido sitio
public class EnviaConecao extends Thread {

    private Conecao conecao;
    private ObjectOutputStream oOS;


    public EnviaConecao(Conecao conecao, ObjectOutputStream oOS) {
        this.conecao = conecao;
        this.oOS = oOS;

        this.start();
    }
    @Override
    public void run() {
        boolean flag = false;
        int tentativas = 0;
        do {
            try {
                oOS.writeObject(conecao);
                oOS.flush();
                flag = true;
                // A rececao da respoosta de sucesso ou falha doi servidor é feita por parte da thread com a classe RecebeMensagensThread
            } catch (IOException e) {
                tentativas++;
            }
        }while(!flag && tentativas < 3);
        if(!flag)
            System.out.println("Não foi possivel conectar com o cliente!");
    }
}
