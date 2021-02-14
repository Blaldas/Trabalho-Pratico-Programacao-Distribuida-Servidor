package typos;

import classesAjuda.ServidoresConectados;
import conecao.Conecao;
import conecaoMultisocketUPD.ConecaoMultisocketUDP;
import main.Main;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*          Explicação
Esta interface guarda todos os código de ligação entre as varioas instancia de servidor e cliente
Ou seja, aquando um cliente manda uma mensagem ao servidor, essa mensagem deveára incluir um destes typos
Dessa forma, o servidor sabe naturalmente como proceder, facilitando o algoritmo de troca de mensagens

ex:
Aquando  do registo de um novo utilizador, o cliente deverá enviar, para além dos dados de registo,
Uma String código com o Typo "#REGISTER"
No lado do servidor, o mesmo obterá a mensagem recibida (provavelmente utilizará uma classe com posta por String + Object)
E lerá essa String. Após ver que esta escrito "#REGISTER" irá tranformar o objecto na classe devida.
*/
public  interface Typos{





    String SEARCH_REQUEST = "SEARCH_REQUEST";   //manda ao servidor colocado para perguntar se pode conectar
    String SEARCH_ANSWER_DENNIED = "SEARCH_ANSWER_DENNIED";    //resposta do servidor para indicar que NÃO ACEITA CONEÇÃO
    String SEARCH_ANSWER_ACCEPTED = "SEARCH_ANSWER_ACCEPTED";    //resposta do servidor para indicar que ACEITA CONEÇÃO



    //fase 1: login e registo:--------------------------------------------------
    //cliente
    String REGISTER = "REGISTER";             //registar user
    String LOGIN = "LOGIN";                    //login do user

    //servidor
    String LOGIN_DENNIED = "LOGIN_DENNIED";     //quando liga ao servidor e os dados de login sao errados
    String REGIST_DENNIED = "REGIST_DENNIED";     //quando liga ao servidor e os dados de registo sao errados
    String LOGIN_ACCEPTED = "LOGIN_ACCEPTED";     //quando liga ao servidor e os dados de login sao aceites
    String REGIST_ACCEPTED = "REGIST_ACCEPTED";     //quando liga ao servidor e os dados de registo sao  ACEITES


    //Listagem---------------------------------------------------
    //MENSAGENS:
    //cliente
    String LIST_DIRECT_MESSAGE = "#LIST_DIRECT_MESSAGE";                    //indica que é um pedido de listagem de mensagens diretas
    //o OBJ da classe Conecao será o int que define quantas mensagens quer listar
    //servidor (resposta)
    String LIST_DIRECT_MESSAGE_SUCESS = "#LIST_DIRECT_MESSAGE_SUCESS";      //caso tenha encontrado corrspondencias (nomes do user)
    //o OBJ é um List<Mensagem> com todas as mensagens
    String LIST_DIRECT_MESSAGE_FAILURE = "#LIST_DIRECT_MESSAGE_FAILURE";    //caso nao tenha encontrado corrsponbdencia (nome do user, talvez outro erro (?))
    //nota: não haver mensagens trocasdas não é erro.
    //CANAIS:


    //LISTAR CANAIS E UTILIZADORES (servidor e cliente)
    String LIST_ALL_CHANNELS_AND_USERS = "#LIST_ALL_CHANNELS_AND_USERS";    //pedido para listar todos os canais e utilizadores
    String LIST_ALL_CHANNELS_AND_USERS_SUCESS = "#LIST_ALL_CHANNELS_AND_USERS_SUCESS";
    String LIST_ALL_CHANNELS_AND_USERS_FAILURE = "#LIST_ALL_CHANNELS_AND_USERS_FAILURE";
    //utiliza uma classe especifica com List de utilizadores (string de nomes) e List de canais(sytring de nomes)
    //Esta tag é usada pelo servidor e pelo cliente

    //MENSAGENS.-----------------------------------------
    String DIRECT_MESSAGE = "#DIRECT_MESSAGE";  //Codigo de mensagem direta-> tanto para envio como paras recec~çao do cliente

    //resposta de sucesso de envio de mensagem ao servidor
    String MENSAGE_SEND_SUCCESS = "#MENSAGE_SEND_SUCCESS";
    String MENSAGE_SEND_FAIL = "#MENSAGE_SEND_FAIL";

    //CANAIS.--------------------------------
    String CREATE_CHANNEL = "#CREATE_CHANNEL";      //criar canal
    String CREATE_CHANNEL_SUCCESS = "#CREATE_CHANNEL_SUCCESS";      //sucesso a criar canal
    String CREATE_CHANNEL_FAIL ="#CREATE_CHANNEL_FAIL";          //falha a criar o canal

    String MESSAGE_CHANNEL = "#MESSAGE_CHANNEL";    //mandar mensagem
    String MESSAGE_CHANNEL_FAIL = "#MESSAGE_CHANNEL_FAIL";
    String MESSAGE_CHANNEL_SUCCESS = "#MESSAGE_CHANNEL_SUCCESS";

    String CHANNEL_LIST_MESSAGES = "#CHANNEL_LIST_MESSAGES";
    String CHANNEL_LIST_MESSAGES_FAIL = "#CHANNEL_LIST_MESSAGES_FAIL";
    String CHANNEL_LIST_MESSAGES_SUCCESS = "#CHANNEL_LIST_MESSAGES_SUCCESS";

    String DELETE_CHANNEL = "#DELETE_CHANNEL";
    String DELETE_CHANNEL_SUCCESS = "#DELETE_CHANNEL_SUCCESS";
    String DELETE_CHANNEL_FAIL = "#DELETE_CHANNEL_FAIL";

    String CHANNEL_STATISTICS = "#CHANNEL_STATISTICS";
    String CHANNEL_STATISTICS_SUCCESS = "#CHANNEL_STATISTICS_SUCCESS";
    String CHANNEL_STATISTICS_FAIL = "#CHANNEL_STATISTICS_FAIL";

    String EDIT_CHANNEL = "#EDIT_CHANNEL";
    String EDIT_CHANNEL_SUCCESS = "#EDIT_CHANNEL_SUCCESS";
    String EDIT_CHANNEL_FAIL = "#EDIT_CHANNEL_FAIL";

    //logica de ligação
    String CONNECTION_CHECK = "#CONNECTION_CHECK";  //used to check if the user is still connected, must be received and sent by both parties
    //the server must periodically go through all the clients connected and send a message with this code
    //the client must recieve the code and send it back
    //if the client does not send it back, the server must assume that the client has disconnected

    //MULTICAST SOCKET
    String ASKING_NUMBER_CLIENTS_CONECTED = "ASKING_NUMBER_CLIENTS_CONECTED";       //usado quando quer saber quantos clientes estao conectados a cada servidor
    String MY_NUMBER_CLIENTS_CONECTED = "#MY_NUMBER_CLIENTS_CONECTED";              //USADO para responder com o numero de clientes conectados
    String PING_REQUEST = "PING_REQUEST";                                           //usado para verificar quais os servidores que estãoa tivos
    String PING_REQUEST_ANSWER = "PING_REQUEST_ANSWER";                             //usado para responder ao ping de servidores

}
