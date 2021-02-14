package TCP;

import baseDados.ConnDB;
import bases.*;
import conecao.Conecao;
import conecao.EnviaConecao;
import typos.Typos;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;


//ela tem como responsabilidade receber mensagens e processar o que fazer com elas
//snedo que tanto pode responder como mostrar informação para o user
//tipo de mensagens que pode receber:
//-checks se user está online
//receber mensagens diretas
//receber mensagens pedidas do servidor
///...
public class RecebeMensagensThread extends Thread {
    List<RecebeMensagensThread> listaThreads;

    private ConnDB baseDados;                   //base de dados

    private ObjectOutputStream oOS;                          //escreve para o servidor
    private ObjectInputStream oIS;                              //recebe do servidor

    private Socket socket;

    private Conecao conecao;
    private boolean logado = false;

    private Utilizador utilizador;

    public RecebeMensagensThread(Socket socket, List<RecebeMensagensThread> listaThreads, ConnDB baseDados) {

        try {
            this.socket = socket;       //guarda socket com dados do utilizador

            this.oOS = new ObjectOutputStream(socket.getOutputStream());
            this.oIS = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.listaThreads = listaThreads;
        //adiciona este utilizador à lista de users logadops
        //Main.listaUtilizadoresConectados.add(new UtilizadorConectado(socket));

        this.baseDados = baseDados;

        start();                                        //comeca a correr (receber e enviar cenas)
    }

    @Override
    public void run() {
        while (true) {
            try {                                           //fica a espera que o cliente mande alguma coisa
                conecao = (Conecao) oIS.readObject();
            } catch (IOException | ClassCastException | ClassNotFoundException e) {
                if (utilizador != null)
                    System.out.println("user <" + utilizador.getNome() + "> desligou-se");
                else
                    System.out.println("cliente <" + socket.getRemoteSocketAddress() + "> desligou-se");
                try {
                    oIS.close();
                    oOS.close();
                    socket.close();
                    break;
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }

            //processamento da mensagem
            //PEDIDO PARA Login:

            switch (conecao.getMsgCode()) {
                case Typos.LOGIN:
//Caso tenha enviado com sucesso
                {
                    System.out.println("recebeu pedido para login");
                    utilizador = (Utilizador) conecao.getObj();         //obtem dados de user


                    //verifica que na base de dados o user esta registado com as mesmas credenciais

                    //altera boolean de login para indicar que esta logado
                    //devolve que esta logado
                    boolean rBd = false;
                    try {
                        rBd = baseDados.LoginUser(utilizador.getNome(), utilizador.getPassword());

                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    Conecao resposta = null;
                    boolean falgLoginAceite = false;
                    if (rBd) {
                        resposta = new Conecao(Typos.LOGIN_ACCEPTED, null);               //cria resposta de accept
                        falgLoginAceite = true;

                    } else
                        resposta = new Conecao(Typos.LOGIN_DENNIED, null);               //cria resposta de accept

                    try {
                        oOS.writeObject(resposta);                                              //envia resposta a dizer que aceitou
                        oOS.flush();
                        if (falgLoginAceite) {
                            this.logado = true;                                                         //coloca boolean de logado a true
                            System.out.println("user <" + utilizador.getNome() + "> logou-se");       //mostra no ecra que aceitou
                        } else {
                            System.out.println("user <" + socket.getRemoteSocketAddress() + "> colocou dados de login errados");       //mostra no ecra que aceitou
                        }
                    } catch (IOException e) {                                                    //em caso de erro
                        e.printStackTrace();
                    }


                    break;
                }
                case Typos.REGISTER: {

                    System.out.println("recebeu pedido para Registo");
                    utilizador = (Utilizador) conecao.getObj();
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
                    if (registoComSucessoFlag)
                        resposta = new Conecao(Typos.REGIST_ACCEPTED, null);               //cria resposta de accept
                    else {
                        System.out.println("não foi possivel fazer registo!");
                        resposta = new Conecao(Typos.REGIST_DENNIED, null);               //cria resposta de denny
                    }
                    try {
                        oOS.writeObject(resposta);                                              //envia resposta a dizer que aceitou
                        oOS.flush();
                        if (registoComSucessoFlag) {
                            this.logado = true;                                                         //coloca boolean de logado a true
                            System.out.println("user <" + utilizador.getNome() + "> registou-se");       //mostra no ecra que aceitou
                        }
                    } catch (IOException e) {                                                    //em caso de erro
                        e.printStackTrace();
                    }


                    break;
                }
                case Typos.DIRECT_MESSAGE: {
                    //caso receba mensagem direta

                    //começa por verificar se o utilizador encontra-se conectado a esteservidor especifico
                    Mensagem mensagem = (Mensagem) conecao.getObj();
                    System.out.println("Recebe pedido para enviar mensagem de <" + mensagem.getNome_enviador() + "> para <" + mensagem.getNome_receptor() + ">");
                    boolean flag = false;                   //flag para saber se esta neste sv ou não

                    ObjectOutputStream oOSReceptor = null;
                    //prcura utilizador na lista
                    for (RecebeMensagensThread listaThread : listaThreads) {
                        if (mensagem.getNome_receptor().equals(listaThread.getUtilizador().getNome())) {        //se encontrar ustilzador na lista
                            flag = true;                            //coloca na flag que encontrou
                            oOSReceptor = listaThread.getOOS();
                            break;
                        }
                    }

                    Conecao resposta;
                    EnviaConecao enviaConecao;
                    //caso user não esteja online, não envia
                    if (!flag) {
                        System.out.println("não foi possivel enviar mensagem de <" + mensagem.getNome_enviador() + "> para <" + mensagem.getNome_receptor() + ">");
                        System.out.println("user <" + mensagem.getNome_receptor() + "> não esta conectado");
                        resposta = new Conecao(Typos.MENSAGE_SEND_FAIL, null);
                    } else {      //caso utilador esteja online
                        System.out.println("mensagem enviada de <" + mensagem.getNome_enviador() + "> para <" + mensagem.getNome_receptor() + ">");
                        resposta = new Conecao(Typos.MENSAGE_SEND_SUCCESS, null);

                        try {
                            baseDados.guardaMensagem(mensagem.getMensagem(), mensagem.getNome_enviador(), mensagem.getNome_receptor());
                            if (oOSReceptor != null) {
                                enviaConecao = new EnviaConecao(conecao, oOSReceptor);
                            }

                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    }
                    enviaConecao = new EnviaConecao(resposta, oOS);


                    // ___________________________________________________________________
                    break;
                }
                case Typos.LIST_DIRECT_MESSAGE:

                    PedidoListaMensagensDiretas listMensagens = (PedidoListaMensagensDiretas) conecao.getObj();
                    System.out.println("Utilizador <" + utilizador.getNome() + "> pediu para rever as <" + listMensagens.getListNumber() + "> mensagens trocadas com utilizador <" + listMensagens.getFonte() + ">");
                    boolean flagErro = false;
                    ArrayList<Mensagem> listamensagens = new ArrayList<>();
                    try {
                        listamensagens = baseDados.listaMensagens(utilizador.getNome(), listMensagens.getFonte(), listMensagens.getListNumber());
                        flagErro = true;
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }

                    Conecao respostas;
                    if (!flagErro) {

                        respostas = new Conecao(Typos.LIST_DIRECT_MESSAGE_FAILURE, null);
                    } else {
                        respostas = new Conecao(Typos.LIST_DIRECT_MESSAGE_SUCESS, new ListaMensagensResposta(listamensagens));
                    }

                    int flagEnviou = 0;
                    do {
                        try {
                            oOS.writeObject(respostas);
                            oOS.flush();
                            flagEnviou = -1;
                        } catch (IOException e) {
                            e.printStackTrace();
                            flagEnviou++;
                        }
                    } while (flagEnviou > 0 && flagEnviou < 3);

                    if (flagEnviou == -1) {
                        System.out.println("lista de mensagens foi enviada para utilizador <" + utilizador.getNome() + "> com sucesso");
                    } else {
                        System.out.println("Erro a enviar lista de mensagens para utilizador <" + utilizador.getNome() + ">");
                    }
                    break;
                case Typos.LIST_ALL_CHANNELS_AND_USERS: {
                    System.out.println("utilizador <" + utilizador.getNome() + "> pediu para que fossem listados todos os utilizadores e canais");
                    Conecao resposta;
                    ArrayList<String> canais = new ArrayList<>();
                    ArrayList<String> users = new ArrayList<>();

                    boolean flagBD = false;
                    try {
                        canais = baseDados.listaTodosCanais();
                        users = baseDados.listaTodosUsers();
                        flagBD = true;
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }


                    ChannelsAndUsersList channelsAndUsersList = new ChannelsAndUsersList(canais, users);
                    if (flagBD)
                        resposta = new Conecao(Typos.LIST_ALL_CHANNELS_AND_USERS_SUCESS, channelsAndUsersList);
                    else
                        resposta = new Conecao(Typos.LIST_ALL_CHANNELS_AND_USERS_FAILURE, channelsAndUsersList);

                    int tries = 0;
                    boolean flag = false;
                    do {
                        try {
                            oOS.writeObject(resposta);
                            oOS.flush();
                            flag = true;
                        } catch (IOException e) {
                            e.printStackTrace();
                            tries++;
                        }
                    } while (!flag && tries < 3);
                    if (!flag)
                        System.out.println("Não fopi possivel responder ao pedido de listar canais e users!");


                    break;
                }
                case Typos.CREATE_CHANNEL: {

                    Canal canal = (Canal) conecao.getObj();

                    System.out.println("pedido feito por <" + utilizador.getNome() + "> para criar um novo canal chamado <" + canal.getNomeCanal() + ">");

                    boolean flagUnico = true;
                    //verifica se nome de canal é unico
                    try {
                        if (baseDados.procuraCanal(canal.getNomeCanal())) {
                            System.out.println("Já existe um canal com, nome <" + canal.getNomeCanal() + ">");
                            flagUnico = false;
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }

                    boolean registaCanal = false;
                    if (flagUnico) {
                        try {
                            baseDados.registaCanal(canal.getNomeCanal(), canal.getPassword(), canal.getNomeOwner(), canal.getDescricao());
                            registaCanal = true;
                            System.out.println("Canal registado na base de dados com sucesso!");
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    }
                    Conecao resposta = null;
                    if (!registaCanal) {                  //caso nao tenha registado na BD

                        resposta = new Conecao(Typos.CREATE_CHANNEL_FAIL, null);
                    } else {
                        resposta = new Conecao(Typos.CREATE_CHANNEL_SUCCESS, null);
                    }
                    System.out.println("A enviar resposta à criacao de canal");
                    EnviaConecao enviaConecao = new EnviaConecao(resposta, oOS);
                    System.out.println(resposta.getMsgCode());


                    break;
                }
                case Typos.MESSAGE_CHANNEL: {
                    MensagemCanal mensagemCanal = (MensagemCanal) conecao.getObj();
                    boolean flagCriacao = false;
                    try {
                        flagCriacao = baseDados.mensagemCanal(mensagemCanal.getNomeCanal(), mensagemCanal.getPassword(), mensagemCanal.getNomeUser(), mensagemCanal.getMensagem());
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }

                    Conecao conecao = null;
                    if (flagCriacao) {    //sucesso
                        conecao = new Conecao(Typos.MESSAGE_CHANNEL_SUCCESS, null);
                        System.out.println("mensagem enviada com sucesso para o canal");
                    } else {
                        conecao = new Conecao(Typos.MESSAGE_CHANNEL_FAIL, null);
                        System.out.println("Não foi possivel enviar mensagem para o canal");
                    }

                    EnviaConecao enviaConecao = new EnviaConecao(conecao, oOS);
                    break;
                }
                case Typos.CHANNEL_LIST_MESSAGES: {
                    PedidoListaMensagensCanal p = (PedidoListaMensagensCanal) conecao.getObj();
                    System.out.println("Pedido recebido para listar <" + p.getNumeroMensagensAListar() + "> mensagens do canal <" + p.getNomeCanal() + ">");

                    boolean dadosCorretos = false;

                    //ve se dados estao corretos
                    try {
                        dadosCorretos = baseDados.LoginCanal(p.getNomeCanal(), p.getPassCanal());
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }

                    Conecao conecao = null;
                    ArrayList<Mensagem> a = null;

                    boolean flagDB = false;
                    if (dadosCorretos) {      //login para o canal funcionou
                        try {
                            a = baseDados.listaMensagensCanal(p.getNomeCanal(), p.getNumeroMensagensAListar());            //obtem as mensagens do canal
                            flagDB = true;
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    }
                    //envia resposta
                    if (flagDB) {
                        ListaMensagensResposta lmr = new ListaMensagensResposta(a);
                        conecao = new Conecao(Typos.CHANNEL_LIST_MESSAGES_SUCCESS, lmr);
                    } else {
                        conecao = new Conecao(Typos.CHANNEL_LIST_MESSAGES_FAIL, null);
                    }

                    EnviaConecao enviaConecao = new EnviaConecao(conecao, oOS);

                    break;
                }
                case Typos.DELETE_CHANNEL: {
                    Canal canal = (Canal) conecao.getObj();
                    System.out.println("foi pedido para elimininar canal <" + canal.getNomeCanal() + ">");


                    //ve se dados de login no canal estao corretos
                    boolean dadosCorretos = false;
                    try {
                        dadosCorretos = baseDados.LoginCanal(canal.getNomeCanal(), canal.getPassword());
                        dadosCorretos = true;
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    //ve se este utilizador é o owner
                    boolean isOwner = false;
                    if (dadosCorretos)
                        try {
                            isOwner = baseDados.isOwnerCanal(canal.getNomeCanal(), canal.getNomeOwner());
                            isOwner = true;
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    //elimina canal da tabela canais
                    boolean eliminaCanal = false;
                    if (isOwner)
                        try {
                            baseDados.eliminaCanal(canal.getNomeCanal());
                            baseDados.eliminachatCanal(canal.getNomeCanal());   //elimina chat do canal da tableca channelschat
                            eliminaCanal = true;
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }

                    Conecao conecaoResposta;
                    if (eliminaCanal) {
                        conecaoResposta = new Conecao(Typos.DELETE_CHANNEL_SUCCESS, null);
                        System.out.println("Canal eliminado com sucesso");
                    } else {
                        conecaoResposta = new Conecao(Typos.DELETE_CHANNEL_FAIL, null);
                        System.out.println("Não foi possivel eliminar canal");
                    }

                    EnviaConecao enviaConecao = new EnviaConecao(conecaoResposta, oOS);

                    break;
                }
                case Typos.CHANNEL_STATISTICS: {
                    DadosEstatisticosCanal dadosEstatisticosCanal = (DadosEstatisticosCanal) conecao.getObj();
                    System.out.println("utilizador <" + utilizador.getNome() + "> pediu para alistar as estatisticas do canal <" + dadosEstatisticosCanal.getNome() + ">");

                    //ve se dados de login no canal estao corretos
                    boolean dadosCorretos = false;
                    try {
                        dadosCorretos = baseDados.LoginCanal(dadosEstatisticosCanal.getNome(), dadosEstatisticosCanal.getPassword());
                        dadosCorretos = true;
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    //alista os dados
                    int nMgs = 0;
                    int nFich = 0;
                    int nUsers = 0;

                    boolean getData = false;
                    if (dadosCorretos)
                        try {
                            nMgs = baseDados.NumeroMensagensCanal(dadosEstatisticosCanal.getNome());
                            nFich = baseDados.NumeroFicheirosCanal(dadosEstatisticosCanal.getNome());
                            nUsers = baseDados.NumeroUsersCanal(dadosEstatisticosCanal.getNome());
                            getData = true;
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    dadosEstatisticosCanal.setNumMsgs(nMgs);
                    dadosEstatisticosCanal.setNumFichs(nFich);
                    dadosEstatisticosCanal.setNumUsers(nUsers);

                    Conecao conecaoResposta;

                    if (getData)
                        conecaoResposta = new Conecao(Typos.CHANNEL_STATISTICS_SUCCESS, dadosEstatisticosCanal);
                    else
                        conecaoResposta = new Conecao(Typos.CHANNEL_STATISTICS_FAIL, dadosEstatisticosCanal);

                    EnviaConecao enviaConecao = new EnviaConecao(conecaoResposta, oOS);
                    System.out.println("foi enviado para o utilizador <" + utilizador.getNome() + "> os dados estatisticos do canal <" + dadosEstatisticosCanal.getNome() + ">");
                    break;
                }
                case Typos.EDIT_CHANNEL: {
                    EditarCanal canal = (EditarCanal) conecao.getObj();
                    //ve se credenciais estao certas
                    //ve se é o owner
                    //muda o que tem a mudar

                    System.out.println("foi pedido para elimininar canal <" + canal.getNome() + ">");


                    //ve se dados de login no canal estao corretos
                    boolean dadosCorretos = false;
                    try {
                        dadosCorretos = baseDados.LoginCanal(canal.getNome(), canal.getPassword());
                        dadosCorretos = true;
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    //ve se este utilizador é o owner
                    boolean isOwner = false;
                    if (dadosCorretos)
                        try {
                            isOwner = baseDados.isOwnerCanal(canal.getNome(), utilizador.getNome());
                            isOwner = true;
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }

                    //muda o que tem de mudar
                    boolean flagOps = false;
                    if (isOwner) {        //muda o que tem de mudar
                        try {
                            if (canal.getNovaDescricao() != null)
                                baseDados.mudarDescricaoCanal(canal.getNome(), canal.getNovaDescricao());

                            if (canal.getPassword() != null)
                                baseDados.mudarPassCanal(canal.getNome(), canal.getNovaPass());

                            flagOps = true;
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    }
                    //resposta
                    Conecao conecaoResposta = null;
                    if (flagOps) {
                        conecaoResposta = new Conecao(Typos.EDIT_CHANNEL_SUCCESS, null);
                        System.out.println("O canal <" + canal.getNome() + "> teve os seus dados atualizados");
                    } else {
                        conecaoResposta = new Conecao(Typos.EDIT_CHANNEL_FAIL, null);
                        System.out.println("Não foi possivel atualizar o canal <" + canal.getNome() + ">");
                    }

                    EnviaConecao enviaConecao = new EnviaConecao(conecaoResposta, oOS);


                    break;
                }
            }
        }
        listaThreads.remove(this);          //remove-se da lista de threads
    }

    public void enviaMsgBroacastRMI(String fonte, String msg) {
        Mensagem mensagem = new Mensagem(fonte, utilizador.getNome(), msg);
        Conecao resposta = new Conecao(Typos.DIRECT_MESSAGE, mensagem);               //cria resposta de accept

        try {
            oOS.writeObject(resposta);                                              //envia resposta a dizer que aceitou
            oOS.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Utilizador getUtilizador() {
        return utilizador;
    }

    public ObjectOutputStream getOOS() {
        return oOS;
    }

    public boolean isLogado() {
        return logado;
    }

    public void setLogado(boolean logado) {
        this.logado = logado;
    }
}