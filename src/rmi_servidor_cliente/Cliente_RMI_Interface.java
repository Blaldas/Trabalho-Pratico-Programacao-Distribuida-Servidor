package rmi_servidor_cliente;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Cliente_RMI_Interface extends Remote {
    public void recebemMensagem(String msg) throws RemoteException;
}
