package rmi_servidor_cliente;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Observer_RMI_Interface extends Remote {
    void notificacao(String description) throws RemoteException;
}
