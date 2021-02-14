package bases;

import conecao.Conteudo;

import java.io.Serializable;

public class EditarCanal extends Conteudo implements Serializable {
    static final long serialVersionUID = 42L;

    private String nome;
    private String password;
    private String novaDescricao;
    private String novaPass;


    public EditarCanal(String nome, String password, String novaDescricao, String novaPass) {
        this.nome = nome;
        this.password = password;
        this.novaDescricao = novaDescricao;
        this.novaPass = novaPass;
    }

    public String getNome() {
        return nome;
    }

    public String getPassword() {
        return password;
    }

    public String getNovaDescricao() {
        return novaDescricao;
    }

    public String getNovaPass() {
        return novaPass;
    }
}
