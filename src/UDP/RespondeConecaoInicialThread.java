package UDP;

import bases.IpAndPort;
import classesAjuda.ClassToByteArray;
import classesAjuda.ListaIpsandPorts;
import classesAjuda.ServidoresConectados;
import conecao.Conecao;
import conecaoMultisocketUPD.ConecaoMultisocketUDP;
import main.Main;
import typos.Typos;

import javax.swing.*;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class RespondeConecaoInicialThread extends Thread {

    private DatagramPacket datagramPacket;                  //datagramm packet recebido
    // private MulticastSocket multicastSocket;
    private List<RespondeConecaoInicialThread> listaRespondeConecaoInicialThread;   //lista com todas aas threads que se estao a processar alguma mensagem


    public RespondeConecaoInicialThread(DatagramPacket datagramPacket, MulticastSocket multicastSocket, List<RespondeConecaoInicialThread> listaRespondeConecaoInicialThread) {
        this.datagramPacket = datagramPacket;           //datagramPacket com os dados do utilizador que fez o peiddo para conecao
        // this.multicastSocket = multicastSocket;         //este multicast socket é o multicastSocketLigacoes da main
        this.listaRespondeConecaoInicialThread = listaRespondeConecaoInicialThread;


        this.start();
    }


    /*
     * recebe da rede pedidos de conecao
     * para o multicast udp, pede a utilização de todos os servidores
     * se algum servidor tiver menos de metade das ligações deste, nega esse pedido,
     * ordena a utilização dos servidores por ordem de utilização (menos usado para mais usado)
     * NÃO COLOCA O SEU IP NESSA LISTA
     * Retorna a lista de servidores e o codigo a indicar se se pode conectar ou não a este servidor
     */
    @Override
    public void run() {
        String address = datagramPacket.getAddress().getHostAddress();  //guarda em adress o ip do cliente
        //System.out.println("responde conecao inical -> run");
        int port = datagramPacket.getPort();                            //guarda em port a port do cliente
        //System.out.println("porto e enderesso:\t"+ datagramPacket.getAddress() + ":" + port);

        Conecao pedido = (Conecao) ClassToByteArray.unSerialize(datagramPacket.getData());      //obtem a claase conecao que foi enviada
        if (pedido == null) {                                                                     //verifica se foi corrompida
            System.out.println("problemas no cast da classe conecao");
            ClassNotFoundException e = new ClassNotFoundException();
            e.printStackTrace();
        }


        if (pedido.getMsgCode().equals(Typos.SEARCH_REQUEST)) {                 //caso tenha sido pedido para conecao TCP

         /*   //transforma a mensagem para bytes[]
            try {

                ConecaoMultisocketUDP conecaoMultisocketUDP = new ConecaoMultisocketUDP(Typos.ASKING_NUMBER_CLIENTS_CONECTED, null, 0);

                //nestas 5 linhas transforma a classe num array de bytes (talvez deva fazer uma classe estatica só para isso)
                ByteArrayOutputStream bStream = new ByteArrayOutputStream();
                ObjectOutput oo = null;
                oo = new ObjectOutputStream(bStream);
                oo.writeObject(conecaoMultisocketUDP);
                oo.close();


                //pede aos servidores multicast o número de users que tem
                datagramPacket = new DatagramPacket(bStream.toByteArray(), bStream.toByteArray().length, address, port);
                multicastSocket.send(datagramPacket);

                sleep(1000);      //espera 1 segundo para que toda a gente consiga responder-> não deve ser a melhor prática, mas não demos nenhuma
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }


                /*
                    Terá outra thread que  tratara de receber isso
                    Quando essa thread receber a mensagem com o código Typos.NUMBER_CLIENTS_CONECTED
                    Vai devolver um objeto do tipo ConeçãoMultisocketUDP que contêm esse valor.
                    O servidor que pediu esse valor, vai colocar esses valores na sua lista de outros servidores e número de conecoes
                    E vai devcolver, ordenadamente pelo numero de coneções, uma lista de ips de servidores ligados à rede
                 */
            /*
            Collections.sort(Main.listaServidoresOnline);       //ordena a lista de servidores baseado na sua "carga"

            //cria uma lista com os IPs dos servidores ordenados (menor carga-> maior carga)
            List<String> ipDosServidoresOrdenados = new ArrayList<>();
            for (ServidoresConectados sv : Main.listaServidoresOnline) {
                ipDosServidoresOrdenados.add(sv.getIp());
            }
            //logica para saber se o cliente se vai conectar ou não:
            String sinal;

            //primeiro if-> caso o servidor com menor carga tenha menos de metade da carga deste servidor
            if (Main.listaUtilizadoresConectados.size() > Main.listaServidoresOnline.get(0).getNumeroUtilizadoresLigados() / 2)
                sinal = Typos.SEARCH_ANSWER_DENNIED;
            else                            //SE O SERVIDOR COM MENOR CARGA TIVER MAIS DE METADE DA CARGA DESTE SERVIDOR, ACEITA

             */
            String sinal = Typos.SEARCH_ANSWER_ACCEPTED;                                //cria string com resposta
            List<IpAndPort> ipDosServidoresOrdenados = new ArrayList<>();               //cria string com ip de servidores vazia
            ipDosServidoresOrdenados.add(new IpAndPort(Main.myAdress, Main.portTCP));   //coloca-se na lista com o ip dos servidores
            ListaIpsandPorts lip = new ListaIpsandPorts(ipDosServidoresOrdenados);      //coloca array de string numa classe que pode ser enviada
            Conecao conecaoResposta = new Conecao(sinal, lip);                          //Cria classe conecao para enviar


            //envia a resposta


            byte[] bStream = ClassToByteArray.serialize(conecaoResposta);   //serializa resposta
            if (bStream == null) {                                             //verifica se foi bem serializado, caso não tenha sido bem serializado
                Exception e = new Exception("problemas a serializar bstream");
                e.printStackTrace();
            } else {                                                           //caso tenha sido bem serializado
                try {                                                          //cria datagrampacket
                    DatagramPacket dPacket = new DatagramPacket(bStream, bStream.length, InetAddress.getByName(address), port);
                    DatagramSocket dSocket = new DatagramSocket();              //cria datagramDocket
                    dSocket.send(dPacket);                                      //envia datagram Socket

                    //Main.listaUtilizadoresConectados.add
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        } else {            //caso tenha sido outra coisa

            System.out.println("recebeu pedido udp mas nao foi para perguntar se era para conecao");
        }

        listaRespondeConecaoInicialThread.remove(this);     //remove-se da lista de threads


    }
}
