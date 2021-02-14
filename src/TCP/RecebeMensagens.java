package TCP;


import UDP.ConecaoInicial;
import baseDados.ConnDB;
import conecao.Conecao;
import main.Main;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

//esta classe é lançada numa thread
//fica à espera de conecoes e lanca uma thread RECEBEMENSAGENSTHREAD quando é conectada
public class RecebeMensagens extends Thread {

    ServerSocket svSocket;
    List<RecebeMensagensThread> listaThreads;
    ConnDB baseDados;

    public RecebeMensagens(ConnDB baseDados) {
        svSocket = null;                               //cria a variavel ServerSocket a ser usada

        this.baseDados = baseDados;                     //passa a base de dados

        //cria o servse socket que trata de criar a liagção TCP nos sockets
        try {
            svSocket = new ServerSocket(0);
            Main.portTCP = svSocket.getLocalPort();     //coloca o porto automatico tcp na main (ainda tenho de ver o porque)
        } catch (IOException e) {                       //caso nao tenha conseguido criar socket
            e.printStackTrace();
            System.exit(-998);                    //sai do programa porque não consegiu criar socketr TCP
        }

        //criação de thread UDP que fica à espera de coneções e indica se podem ligar por TCP ou não
        //CRIA O SOCKET TCP ANTES DO UDP, GARANTIDO QUE, SE O UDP ACEITAR, O TCP ESTA PROTO PARA ACEITAR
        ConecaoInicial conecaoInicial = new ConecaoInicial(Main.port, Main.multicastSocketLigacoes);
        listaThreads = new CopyOnWriteArrayList<RecebeMensagensThread>();  //cpo

        this.start();
    }


    //espera por receber coneções.
    //adiciona socket da ligação a lista de clientes conectados
    //lanca thread que processa a conecao quando recebe
    @Override
    public void run() {
        Socket socket = null;                                       //cria a varialvel socket a ser usada

        //cria as threads que tratam de processar as lkigações TCP
        while (true) {
            try {
                socket = svSocket.accept();
                // new thread for a client
                System.out.println("recebeu conecao tcp");
                listaThreads.add(new RecebeMensagensThread(socket, listaThreads, baseDados));
            } catch (IOException e) {
                System.out.println("I/O error: " + e);
            }

        }
    }

    //envia mensagem para todos
    public void msgBroadCast(String origem, String msg) {
        for (RecebeMensagensThread e : listaThreads) {
            e.enviaMsgBroacastRMI(origem, msg);
        }
    }

}
