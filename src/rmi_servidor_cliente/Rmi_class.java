package rmi_servidor_cliente;

import TCP.RecebeMensagens;
import baseDados.ConnDB;
import conecao.Conecao;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Rmi_class extends UnicastRemoteObject implements Server_RMI_Interface {
    ConnDB baseDados;
    RecebeMensagens recebeMensagens;
    List<Observer_RMI_Interface> observers;
    List<Cliente_RMI_Interface> clientes;

    public Rmi_class() throws RemoteException {
        observers = new ArrayList<>();
        clientes = new ArrayList<>();
    }


    public void setBDESetMsgs(ConnDB bd, RecebeMensagens recebeMensagens) throws RemoteException {
        baseDados = bd;
        this.recebeMensagens = recebeMensagens;
    }


    @Override
    public void registaCliente(UtilizadorRMI utilizador, Cliente_RMI_Interface cri) throws RemoteException {
        System.out.println("recebeu pedido para Registo de <" + utilizador.getNome() + "> por RMI");
        boolean registoComSucessoFlag = false;                  //flag que vai indicar que resposta dar no fim
        boolean flagnomeunico = false;

        //verifica se já existe algum utilizador com o mesmo nome
        try {
            flagnomeunico = !baseDados.procuraUser(utilizador.getNome());   //se a bd encontrar da return a true, logo tem de ser !funcao
            if (!flagnomeunico)
                System.out.println("Nome <" + utilizador.getNome() + "> já esta em uso");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        if (flagnomeunico)           //caso o nome seja unico
        {
            try {
                utilizador.guardaFoto();        //tenta guardar a foto no disco
                String local = new String(utilizador.getFotoLocation(), 0, utilizador.getFotoLocation().length);
                baseDados.registaUser(utilizador.getNome(), utilizador.getPassword(), local);
                registoComSucessoFlag = true;
            } catch (IOException | SQLException e) {
                e.printStackTrace();
                registoComSucessoFlag = false;           //coloca o sucesso a true;
            }
        }

        Conecao resposta;
        if (registoComSucessoFlag) {

            notificaObservers("utilizador <" + utilizador.getNome() + "> registou-se com sucesso");
            System.out.println("utilizador <" + utilizador.getNome() + "> registou-se com sucesso");               //cria resposta de accept
            clientes.add(cri);
            cri.recebemMensagem("Server rmi: Utilizador criado com sucesso");
            return;
        }
        notificaObservers("não foi possivel fazer registo de <" + utilizador.getNome() + ">!");
        System.out.println("não foi possivel fazer registo de <" + utilizador.getNome() + ">!");
        cri.recebemMensagem("Server rmi: Não foi possivel registar utilizador");



    }

    /**
     * @param name:         nome do utilizador RMI
     * @param msgBroadcast: mensagem a enviar
     *                      <p>
     *                      manda mensagem por broadcast para clientes normais meta 1
     *                      notifica obsersers
     *                      manda mensagem para clientes rmi
     *                      Regista mensagem na base de dados
     */
    @Override
    public void broadCastUtilizadoresRMI(String name, String msgBroadcast) throws RemoteException {
        recebeMensagens.msgBroadCast(name, msgBroadcast);
        notificaObservers("Utilizador <"+name+"> fez broadCast da mensagem <" + msgBroadcast + ">");

        for (Cliente_RMI_Interface cri : clientes) {
            cri.recebemMensagem("Utilizador <"+name+"> fez broadCast da mensagem <" + msgBroadcast + ">");
        }

        //registar na base de dados
        try {
            baseDados.guardaMensagem(msgBroadcast, name, "broadCast");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void addObserver(Observer_RMI_Interface observer) throws RemoteException {
        if (!observers.contains(observer)) {
            observers.add(observer);
            System.out.println("observador adicionado");
        }
    }

    @Override
    public synchronized void removeObserver(Observer_RMI_Interface observer) throws RemoteException {
        if (observers.remove(observer))
            System.out.println("observador removido.");
    }

    public synchronized void notificaObservers(String msg) {
        int i;

        for (i = 0; i < observers.size(); i++) {
            try {
                observers.get(i).notificacao(msg);
            } catch (RemoteException e) {
                observers.remove(i--);
                System.out.println("foi removido um observador (observador inacessivel).");
            }
        }
    }
}
