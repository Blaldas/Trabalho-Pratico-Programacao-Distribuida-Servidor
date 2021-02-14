package UDP;

import main.Main;
import typos.Typos;

import java.io.*;
import java.net.*;
import java.util.*;


//clase usada no incio do programa que obtem indicação de todos os servidores online e disponiveis para conecao
public class ConecaoInicial extends Thread {
    private int port;
    private MulticastSocket multicastSocket;

    private DatagramSocket datagramSocket;
    private byte[] buffer;
    private DatagramPacket datagramPacket;

    private List<RespondeConecaoInicialThread> listaRespondeConecaoInicialThread;


    //NOTA: BASTA APENAS CHAMAR O CONSTRUTOR PAHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH
    public ConecaoInicial(int port, MulticastSocket multicastSocket) {               //     ||||
        this.port = port;                                                           //     ||||
        this.multicastSocket = multicastSocket;                                     //     ||||
        this.start();       //<<<<<<<<<<<<<<===================================================
    }


    /*
     * WARNING: esta classe thread apenas recebe mensagens e lanca-as para outra thread de processamento, para garantir concorrencia
     *
     *
     * na outra thread lançada:
     *
     * recebe da rede pedidos de conecao
     * para o multicast udp, pede a utilização de todos os servidores
     * se algum servidor tiver menos de metade das ligações deste, nega esse pedido,
     * ordena a utilização dos servidores por ordem de utilkização (menos usado para mais usado)
     * NÃO COLOCA O SEU IP NESSA LISTA
     * Retorna a lista de servidores e o codigo a indicar se se pode conectar ou não a este servidor
     */
    public void run() {
        buffer = new byte[1024];                //cria buffer
        datagramPacket = new DatagramPacket(buffer, buffer.length);     //cria datagrampacket com buffer

        try {                   //tenta criar datagram socket
            datagramSocket = new DatagramSocket(Main.port);
        } catch (SocketException e) {                                   //se o porto já estiver a ser usado
            System.out.println("Erro ao criar datagramSocket:\nEsta porta já está ocupada");
            try {
                datagramSocket = new DatagramSocket();                   //tenta criar com porto automático
            } catch (SocketException socketException) {                  //SE FOR OUTRO ERRO-> DESISTE
                System.out.println("erro a criar porta com valor automativo");
                socketException.printStackTrace();
                System.exit(-999);                                  //e desliga o porgrama com error code -999
            }

        }
                                                                        //mostra a porta escolhida no final
        System.out.println("porto de conecaoInicial do servidor:\t" + datagramSocket.getLocalPort());
        Main.port = datagramSocket.getLocalPort();                      //gurda na variavelk main a porta escoljhida no final

        try {                                                           //mostra para o utilizador o endereço do servidor
            System.out.println("Adress de conecaoInicial do servidor:\t" + InetAddress.getLocalHost().getHostAddress());
            Main.myAdress = InetAddress.getLocalHost().getHostAddress();    //guarda na main o endereço do servidor
        } catch (UnknownHostException e) {                              //caso haja esse tipo de erro
            e.printStackTrace();
        }

        //cria uma lista para guardar todas as threads ativas (permite desliga-las caso tenhamos de desligar o programa)
        listaRespondeConecaoInicialThread = new ArrayList<RespondeConecaoInicialThread>();

        //loop infinito que espera por pedidos de ligação
        System.out.println("entra no loop infino que espera pedido de ligacao:");
        while (true) {

            try {
                datagramSocket.receive(datagramPacket);                         //obtem ligação inicial
                //chama lista que processa essa ligacao inicial (para concorrencia)
                RespondeConecaoInicialThread a = new RespondeConecaoInicialThread(datagramPacket, multicastSocket, listaRespondeConecaoInicialThread);
                listaRespondeConecaoInicialThread.add(a);                       //acidiona essa classe a lista

            } catch (IOException e) {       //quando o socket é fechado para que se possa desligar o programa
                break;                      //sai do loop de procura
            }

        }

    }


}
