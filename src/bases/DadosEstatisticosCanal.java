package bases;

import conecao.Conteudo;

import java.io.Serializable;

public class DadosEstatisticosCanal extends Conteudo implements Serializable {
    static final long serialVersionUID = 42L;

    private String nome;
    private String password;
    private int numMsgs;                 //a ser indicado do lado do servidor
    private int numFichs;                //a ser indicado do lado do servidor
    private int numUsers;                //a ser indicado do lado do servidor

    public DadosEstatisticosCanal(String nome, String password) {
        this.nome = nome;
        this.password = password;
        numMsgs = -1;
        numFichs = -1;
        numUsers = -1;
    }

    public String getNome() {
        return nome;
    }

    public String getPassword() {
        return password;
    }

    public int getNumUsers() {
        return numUsers;
    }

    public void setNumUsers(int numUsers) {
        this.numUsers = numUsers;
    }

    public int getNumFichs() {
        return numFichs;
    }

    public void setNumFichs(int numFichs) {
        this.numFichs = numFichs;
    }

    public int getNumMsgs() {
        return numMsgs;
    }

    public void setNumMsgs(int numMsgs) {
        this.numMsgs = numMsgs;
    }

    @Override
    public String toString() {
        return "O canal <"+nome+"> tem <"+numUsers+"> users, <"+numMsgs+"> mensagens trocadas e <"+numFichs+"> ficheiros trocados";
    }
}
