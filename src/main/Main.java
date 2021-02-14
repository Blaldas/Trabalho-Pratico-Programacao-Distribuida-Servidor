package main;

import TCP.RecebeMensagens;
import TCP.RecebeMensagensThread;
import baseDados.ConnDB;
import classesAjuda.ServidoresConectados;
import conecaoMultisocketUPD.MulticketLigacao;
import rmi_servidor_cliente.Rmi_class;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

/*
            // Serialize to a byte array

            Colocar o que se quer na classe Conecao

            ByteArrayOutputStream bOUS = new ByteArrayOutputStream();
            ObjectOutput oO = new ObjectOutputStream(bOUS);
            oO.writeObject(conecao);
            oO.close();
            byte buffer[] = bOUS.toByteArray();

            enviar o buffer (ou passar para string)


 */
public class Main {
    static ConnDB baseDados;


    private static boolean exit = false;                                    //exit
    public static int port;
    public static int portTCP;
    public static String myAdress;
    static InetAddress groupDB, groupLigacao;
    static MulticastSocket multicastSocketUpdateBD;         //usado para tarefas de atualizar a base de dados
    public static MulticastSocket multicastSocketLigacoes;         //usado para tarefas de atulizar outros servidores sobre o numero de coneções
    public static MulticastSocket multicastSocketCheck;

    //public static List<UtilizadorConectado> listaUtilizadoresConectados;
    public static List<ServidoresConectados> listaServidoresOnline;


    public static void main(String[] argv) {
        //Dados do servidor
        //port = Integer.parseInt(argv[1]);             //port é o 1 argumento da linha de comandos
        port = 9999;

        //DADOS DA REDE
        //listaUtilizadoresConectados = new ArrayList<>();    //inicia a lista de utilizadores conectados
        listaServidoresOnline = new CopyOnWriteArrayList<>();          //inicia lista de servidores online

        //conecta à base de dados
        System.out.println("A conectar à base de dados...");
        try {
            System.out.println("Indique o endereço da base de dados:");
            Scanner sc = new Scanner(System.in);
            String endereco = sc.nextLine();
            baseDados = new ConnDB(endereco);
        } catch (SQLException throwables) {

            System.out.println("Não foi possivel conectar à base de dados");
            throwables.printStackTrace();
            System.exit(-997);                      //se a conecao com abase de dados falhar saí do programa
        }





        //ligação multicast para verifivcar se a quantidade de servidores é a mesma(PINGA SERVIDORES)
        try {
            multicastSocketCheck = new MulticastSocket();
            //MulticketLigacao multiPingServers = new MulticketLigacao(multicastSocketCheck, listaServidoresOnline);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erro ao fazer ligação multicast.");
            System.exit(-996);//se falhar a ligação multicast, sai do programa
        }



        //liga o tcp
        RecebeMensagens recebeMensagens = new RecebeMensagens(baseDados);


        Scanner sc = new Scanner(System.in);

        //após ligar à base de dados, liga ao RMI!
        try {
            Rmi_class st = new Rmi_class();//cria a instancia da classe que vasi ser usada
            st.setBDESetMsgs(baseDados, recebeMensagens);   //para lhe dar acesso a base de dados e threads oara mensagens
            Registry r = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);     //cria um registry
            r.rebind("rmi//127.0.0.1:1099/ServidorRede", st);                    //mostra no rmi a classe que herda UnicastRemoteObject e a interface
            System.out.println("RMI server is running");

        }catch (RemoteException e) {
            e.printStackTrace();
        }


        String exit;
        while (true) {
            exit = sc.nextLine();
            if (exit.equals("exit")) ;
            break;
        }

        System.exit(-12);
        //fecha as threads todas
    }


    public static boolean getExit() {
        return exit;
    }

    public static void setExit(boolean exit) {
        Main.exit = exit;
    }
}
