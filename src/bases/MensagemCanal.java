package bases;

import conecao.Conteudo;

public class MensagemCanal extends Conteudo {
    static final long serialVersionUID = 42L;

    private final String nomeUser;
    private final String nomeCanal;
    private final String password;
    private final String mensagem;


    public MensagemCanal(String nomeUser, String nomeCanal, String password, String mensagem) {
        this.nomeUser = nomeUser;
        this.nomeCanal = nomeCanal;
        this.password = password;
        this.mensagem = mensagem;
    }

    public String getMensagem() {
        return mensagem;
    }

    public String getPassword() {
        return password;
    }

    public String getNomeCanal() {
        return nomeCanal;
    }

    public String getNomeUser() {
        return nomeUser;
    }
}
