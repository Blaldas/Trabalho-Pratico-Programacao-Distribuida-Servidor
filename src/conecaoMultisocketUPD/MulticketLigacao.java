package conecaoMultisocketUPD;


import classesAjuda.ClassToByteArray;
import classesAjuda.ServidoresConectados;
import conecao.Conecao;
import main.Main;
import typos.Typos;

import java.io.*;
import java.net.*;
import java.util.*;

import static typos.Typos.PING_REQUEST;
import static typos.Typos.PING_REQUEST_ANSWER;

/*
Esta classe é utilizada com o entuito de receber os dados da ligação multicastUDP responsavel por indicar quantos clientes tem cada server
 */
public class MulticketLigacao extends Thread{

    private MulticastSocket multicastSocketLigacao;
    private List<ServidoresConectados> servidoresConectados;
    private HashMap<String, Boolean> mapa;
    private String multicastIp;


    public MulticketLigacao(MulticastSocket multicastSocketLigacao, List<ServidoresConectados> servidoresConectados) throws UnknownHostException {
        this.multicastSocketLigacao = multicastSocketLigacao;
        this.servidoresConectados = servidoresConectados;
        this.multicastIp = "224.0.0.1";

        mapa = new HashMap<>();
        var array = this.servidoresConectados;
        for(int i = 0; i < servidoresConectados.size(); i++){
            mapa.put(array.get(i).getIp(), false);
        }

        pinga();
    }


    public void pinga() throws UnknownHostException {

        final InetAddress group =InetAddress.getByName(multicastIp);
        try {
            multicastSocketLigacao.joinGroup(group);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        new Thread(() -> {
            try{

                for(int i = 0; i < servidoresConectados.size(); i++){//atualiza valores
                    var chave = servidoresConectados.get(i).getIp();
                    mapa.put(chave, false);
                }

//                InetAddress group = InetAddress.getByName(multicastIp);
//                multicastSocketLigacao.joinGroup(group);
                ConecaoMultisocketUDP ping = new ConecaoMultisocketUDP(PING_REQUEST, null, 777);
                byte [] buff = ClassToByteArray.serialize(ping);
                DatagramPacket dpPing = new DatagramPacket(buff, buff.length, group, multicastSocketLigacao.getPort()+2);
                while(true){
                    Thread.sleep(5000);
                    multicastSocketLigacao.send(dpPing);
                    System.out.println("Ping enviado com sucesso. (verifica servers)");
                    Thread.sleep(3000);//dá tempo para que a thread que recebe o pedido atualize o hashmap
                    updateServerList(servidoresConectados, mapa);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
//                InetAddress group = InetAddress.getByName(multicastIp);
//                multicastSocketLigacao.joinGroup(group);
                byte [] buff = new byte[1024];
                DatagramPacket dpResposta = new DatagramPacket(buff, buff.length, group, multicastSocketLigacao.getPort()+3);
                System.out.println("EU ESTOU AQUI");
                while(true){
                    multicastSocketLigacao.receive(dpResposta);
                    System.out.println("Ping recebido com sucesso. (verifica servers)");
                    ConecaoMultisocketUDP resposta = (ConecaoMultisocketUDP) ClassToByteArray.unSerialize(dpResposta.getData());
                    if(resposta.getMsgCode().equals(PING_REQUEST)){
                        mapa.put(multicastIp, true);//atualiza ou insere um novo valor
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private synchronized void updateServerList(List<ServidoresConectados> list, HashMap<String, Boolean> mapa){
        Iterator it = mapa.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry pair = (Map.Entry)it.next();
            if(list.contains(pair.getKey()) && (Boolean)pair.getValue() == false){
                list.remove(pair.getKey());
                mapa.remove(pair.getKey());
            }
        }
    }

    @Override
    public void run() {

//        while(true){
//            byte[] recieved = new byte[1024];
//            DatagramPacket datagramPacket = new DatagramPacket(recieved, recieved.length);
//
//            try {
//                multicastSocketLigacao.receive(datagramPacket);
//
//
//            } catch (IOException e) {
//                System.out.println("MultisocketLigação -> run() -> catch IOExceptionxception");
//                e.printStackTrace();
//            }
//
//            //transforma o array de bytes na classe pretendida
//
//            ConecaoMultisocketUDP mensagemRecebida = null;
//
//            ByteArrayInputStream bis = new ByteArrayInputStream(recieved);
//            ObjectInput in = null;
//            try {
//                in = new ObjectInputStream(bis);
//                mensagemRecebida = (ConecaoMultisocketUDP) in.readObject();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
//            } finally {
//                try {
//                    if (in != null) {
//                        in.close();
//                    }
//                } catch (IOException ex) {
//                    // ignore close exception
//                }
//            }
//
//            //VE OQUE QUE A MENSAGEM DIZ:
//            if (mensagemRecebida == null) {     //caso não tenha conseguido ler
//                System.out.println("MultisocketLigacao.java -> obtem ligacao-> problema a ler a classe recebida...");
//            }
//            else if (mensagemRecebida.getMsgCode().equals(Typos.ASKING_NUMBER_CLIENTS_CONECTED)){
//                //caso esteja a pedir para indicar o numero de clientes que tenho conectados
//                //cria classe com a resposta ao pedido
//          /*
//                ServidoresConectados servidoresConectados = new ServidoresConectados(Main.myAdress, Main.listaUtilizadoresConectados.size());
//                ConecaoMultisocketUDP cResposta = new ConecaoMultisocketUDP(Typos.MY_NUMBER_CLIENTS_CONECTED, servidoresConectados, 0);
//
//                byte[] r = ClassToByteArray.serialize(cResposta);
//                if(r == null){
//                    System.out.println("Não foi possivel serializar objeto!\t MulticketLigacao.java -> run() -> responde ao pedido para saber numero de utilizadores conectados");
//                }
//
//                datagramPacket = new DatagramPacket(r, r.length);
//
//                try {
//                    multicastSocketLigacao.send(datagramPacket);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//
//           */
//
//            }
//            else if (mensagemRecebida.getMsgCode().equals(Typos.MY_NUMBER_CLIENTS_CONECTED)){
//                //quando pedem numero de clientes deste servidor
//
//                //unserialize
//                ServidoresConectados servidoresConectados = (ServidoresConectados) ClassToByteArray.unSerialize(datagramPacket.getData());
//                if(servidoresConectados == null){
//                    System.out.println("PACOTE <MY_NUMBER_CLIENTS_CONECTED> corrompido");
//                }
//
//                //caso unserialized com sucesso
//                //prcura na lista se o servidor existe
//                String address = servidoresConectados.getIp();
//                int flag = 0;
//                for(ServidoresConectados sv :Main.listaServidoresOnline) {
//                    if (sv.getIp().equals(address)) {       //se o servidor existir, atualiza numero de user conectados
//                        sv.setNumeroUtilizadoresLigados(servidoresConectados.getNumeroUtilizadoresLigados());
//                        flag = 1;
//                        break;
//                    }
//                }
//                if(flag != 1)   //se o servidor não existir, adiciona
//                    Main.listaServidoresOnline.add(servidoresConectados);
//
//            }

        InetAddress mGroup = null;
        try {
            mGroup = InetAddress.getByName(multicastIp);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        ReceiveThread reciveThread = new ReceiveThread(multicastSocketLigacao, mGroup);//cria thread para receber pedidos

        try {


            while(true) {
                //envia ping para os servidores
                ConecaoMultisocketUDP ping = new ConecaoMultisocketUDP(PING_REQUEST, null, 777);

                byte [] data = ClassToByteArray.serialize(ping);
                DatagramPacket dp = new DatagramPacket(data, data.length, mGroup, multicastSocketLigacao.getPort());
                multicastSocketLigacao.send(dp);

//                multicastSocketLigacao.setSoTimeout(0);

//                DatagramPacket dpResposta = new DatagramPacket(new byte[1024], 1024);
//                multicastSocketLigacao.receive(dpResposta);
//                ByteArrayInputStream bais = new ByteArrayInputStream();
//                ObjectInputStream ois = new ObjectInputStream(bais);


            }

        }catch(IOException e) {
            e.printStackTrace();
        }
    }


    class ReceiveThread extends Thread{
        private MulticastSocket mSocketReceive;
        private InetAddress mGroup;
        public ReceiveThread(MulticastSocket socket, InetAddress ip){
            mSocketReceive = socket;
            mGroup = ip;
            this.start();
        }

        @Override
        public void run(){

            try {
                while(true) {
                    DatagramPacket dp = new DatagramPacket(new byte[1024], 1024);
                    mSocketReceive.receive(dp);
                    byte[] buff = dp.getData();
                    ByteArrayInputStream bais = new ByteArrayInputStream(buff);
                    ObjectInputStream bis = new ObjectInputStream(bais);
                    ConecaoMultisocketUDP resposta = (ConecaoMultisocketUDP) bis.readObject();

                    String strResposta = resposta.getMsgCode();
                    if(strResposta.equals(PING_REQUEST)){
                        ConecaoMultisocketUDP respostaAoPing = new ConecaoMultisocketUDP(PING_REQUEST_ANSWER, multicastIp, 778);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ObjectOutputStream oos = new ObjectOutputStream(baos);
                        oos.writeObject(respostaAoPing);
                        byte [] buffResposta = baos.toByteArray();
                        DatagramPacket dpResposta = new DatagramPacket(buffResposta, buffResposta.length, mGroup, mSocketReceive.getPort());
                        multicastSocketLigacao.send(dpResposta);
                    }

                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

        }

    }

}
