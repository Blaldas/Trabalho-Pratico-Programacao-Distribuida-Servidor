package bases;

import conecao.Conteudo;

public class Canal extends Conteudo {
    static final long serialVersionUID = 42L;

    private final String nomeCanal;
    private final String nomeOwner;
    private final String descricao;
    private final String password;

    public Canal(String nomeCanal, String nomeOwner, String descricao, String password) {
        this.nomeCanal = nomeCanal;
        this.nomeOwner = nomeOwner;
        this.descricao = descricao;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getNomeOwner() {
        return nomeOwner;
    }

    public String getNomeCanal() {
        return nomeCanal;
    }
}
