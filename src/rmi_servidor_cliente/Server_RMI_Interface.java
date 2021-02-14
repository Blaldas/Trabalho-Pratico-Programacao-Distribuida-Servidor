package rmi_servidor_cliente;

import TCP.RecebeMensagens;
import baseDados.ConnDB;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Server_RMI_Interface extends Remote {

    void registaCliente( rmi_servidor_cliente.UtilizadorRMI uti, Cliente_RMI_Interface cri) throws RemoteException;             //usada para criar novos utilizadores por RMI
    void broadCastUtilizadoresRMI(String name, String msgBroadcast) throws RemoteException;     //usado para enviar a mensagem para todos os servidores
    void addObserver(Observer_RMI_Interface observer) throws RemoteException;
    void removeObserver(Observer_RMI_Interface observer) throws RemoteException;
}
