package bases;

import conecao.Conteudo;

import java.io.Serializable;

public class Mensagem extends Conteudo implements Serializable {
    static final long serialVersionUID = 42L;
    private String nome_enviador;
    private String nome_receptor;
    private String mensagem;



    public Mensagem(String nome_enviador, String nome_receptor, String mensagem) {
        this.nome_enviador = nome_enviador;
        this.nome_receptor = nome_receptor;
        this.mensagem = mensagem;

    }


    public String getNome_enviador() {
        return nome_enviador;
    }
    public String getNome_receptor() {
        return nome_receptor;
    }
    public String getMensagem() {
        return mensagem;
    }

    @Override
    public String toString() {
        String str = "\nDe:\t" + nome_enviador;
        str += "\nPara:\t" + nome_receptor;
        str += "\nmensagem:\t" + mensagem;
        return str;
    }
}
