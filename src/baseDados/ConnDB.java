package baseDados;

import bases.Mensagem;

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class ConnDB {
    //private final String DATABASE_URL = "jdbc:mysql://db4free.net/tppd2020";
    private String DATABASE_URL = "jdbc:mysql://localhost:3306/tppd2020";    //3306
    private final String USERNAME = "pduser12345";
    private final String PASSWORD = "pd-pass-123";

    private Connection dbConn;

    public ConnDB(String endereco) throws SQLException {
        //fazer ligação à base de dados
        DATABASE_URL = "jdbc:mysql://" + endereco + ":3306/tppd2020";
        dbConn = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
        System.out.println("ligação feita com sucesso à base de dados");

    }

    public void close() throws SQLException {
        // fechar a coneção quando não se precisa mais dela
        if (dbConn != null)
            dbConn.close();
    }

    /*
    Caso dados estejam bem: return true
    Caso esteja mal: return false
     */
    public boolean LoginUser(String nome, String password) throws SQLException {
        //select basico com toda a informação na tabela
        Statement st = dbConn.createStatement();
        //o select que se quer da base de dados
        String sqlQuery = "SELECT * FROM users ";

        //caso haja uma condição extra
        if (nome != null)
            sqlQuery += "WHERE name = '" + nome + "'";


        //realiza o comando, devolve um "conjunto de resultados"
        ResultSet rs = st.executeQuery(sqlQuery);

        if (!rs.next())           //move o cursor para a primeira linha e verifica se esta vazio ou não
            return false;                    //se estiver vazio é porque não existe username, logo a conta não existe

        //so deve aparecer uma coluna no rs
        //obtem os dados

        String name = rs.getString("name");
        String pass = rs.getString("password");
        //fecha os recursos
        rs.close();
        st.close();


        //verifica se a palavra passe esta bem
        if (nome.equals(name) && pass.equals(password))
            return true;                                        //caso a palavra pass esteja bem
        return false;                                           //caso a palavra passe não este bem

    }

    //return true: user já existe
    //return false: user não existe
    public boolean procuraUser(String nome) throws SQLException {
        Statement st = dbConn.createStatement();

        String sqlQuery = "SELECT name FROM users WHERE name = '" + nome + "'";

        //realiza o comando, devolve um "conjunto de resultados"
        ResultSet rs = st.executeQuery(sqlQuery);

        boolean r = rs.next();              //se não tiver encontrado alguem com esse nome
        rs.close();
        st.close();
        return r;

    }


    //NOTA: não faz verificação se o user já existe ou não!
    public synchronized void registaUser(String name, String password, String photoPath) throws SQLException {

        Statement st = dbConn.createStatement();
        String sqlQuery = "INSERT INTO users VALUES (0,'" + name + "','" + password + "','" + photoPath + "')";
        st.executeUpdate(sqlQuery);
        st.close();
        return;


    }


    //entra no canal
    //devolve true: dados corretos
    //devolve false: dados errados
    public boolean LoginCanal(String nome, String password) throws SQLException {
        //select basico com toda a informação na tabela
        Statement st = dbConn.createStatement();
        //o select que se quer da base de dados
        String sqlQuery = "SELECT * FROM canais WHERE name = '" + nome + "'";

        //realiza o comando, devolve um "conjunto de resultados"
        ResultSet rs = st.executeQuery(sqlQuery);

        if (!rs.next()) {
            return false;
        }

        //so deve aparecer um
        //int id = rs.getInt("id");
        //String name = rs.getString("name");
        String pass = rs.getString("password");

        rs.close();
        st.close();

        return pass.equals(password);

    }

    public void guardaMensagem(String mensagem, String autor, String receptor) throws SQLException {
        Statement st = dbConn.createStatement();
        String sqlQuery = "INSERT INTO mensagens VALUES (0 ,0 ,'" + autor + "','" + receptor + "','" + mensagem + "')";
        st.executeUpdate(sqlQuery);
        st.close();
    }

    //return true: canal já existe
    //return false: canal não existe
    public boolean procuraCanal(String nome) throws SQLException {
        Statement st = dbConn.createStatement();

        String sqlQuery = "SELECT name FROM canais WHERE name = '" + nome + "'";

        //realiza o comando, devolve um "conjunto de resultados"
        ResultSet rs = st.executeQuery(sqlQuery);

        boolean r = rs.next();
        rs.close();
        st.close();
        return r;

    }

    public void registaCanal(String name, String password, String owner, String descricao) throws SQLException {

        Statement st = dbConn.createStatement();
        String sqlQuery = "INSERT INTO canais VALUES (0,'" + owner + "','" + name + "','" + password + "',' " + descricao + "')";
        st.executeUpdate(sqlQuery);
        st.close();

    }

    public ArrayList<Mensagem> listaMensagens(String user1, String user2, int listNumber) throws SQLException {

        Statement st = dbConn.createStatement();

        String sqlQuery = "SELECT data, autor, receptor, conteudo FROM mensagens";          //select from
        sqlQuery += " WHERE autor = '" + user1 + "' AND receptor = '" + user2 + "' ";           //select enviadas por user1
        sqlQuery += "OR autor = '" + user2 + "' AND receptor = '" + user1 + "'";              //select enviadas por user2
        sqlQuery += " ORDER BY STR_TO_DATE(data,\"%d-%m-%Y\") DESC ";                         //ordena novo-> velho

        //realiza o comando, devolve um "conjunto de resultados"
        ResultSet rs = st.executeQuery(sqlQuery);

        ArrayList<Mensagem> listaMensagens = new ArrayList<Mensagem>();
        int msgCount = 0;
        while (rs.next() && msgCount < listNumber)              //paras as mensagens encontradas, ate não haver mais ou não ser preciso mais
        {
            msgCount++;
            //obtem dados de mensagens
            String autor = rs.getString("autor");
            String receptor = rs.getString("receptor");
            String contudo = rs.getString("conteudo");

            listaMensagens.add(new Mensagem(autor, receptor, contudo));
        }
        rs.close();
        st.close();

        return listaMensagens;

    }

    public ArrayList<String> listaTodosUsers() throws SQLException {

        Statement st = dbConn.createStatement();

        String sqlQuery = "SELECT name FROM users";          //select from


        //realiza o comando, devolve um "conjunto de resultados"
        ResultSet rs = st.executeQuery(sqlQuery);

        ArrayList<String> listaMensagens = new ArrayList<String>();
        int msgCount = 0;
        while (rs.next())              //paras as mensagens encontradas, ate não haver mais ou não ser preciso mais
        {
            msgCount++;
            //obtem dados de mensagens
            listaMensagens.add(rs.getString("name"));
        }
        rs.close();
        st.close();

        return listaMensagens;
    }

    public ArrayList<String> listaTodosCanais() throws SQLException {

        Statement st = dbConn.createStatement();

        String sqlQuery = "SELECT name FROM canais";          //select from


        //realiza o comando, devolve um "conjunto de resultados"
        ResultSet rs = st.executeQuery(sqlQuery);

        ArrayList<String> listaCanais = new ArrayList<String>();
        int msgCount = 0;
        while (rs.next())              //paras as mensagens encontradas, ate não haver mais ou não ser preciso mais
        {
            msgCount++;
            //obtem dados de mensagens
            listaCanais.add(rs.getString("name"));
        }
        rs.close();
        st.close();

        return listaCanais;
    }

    public boolean mensagemCanal(String nomeCanal, String password, String nomeUser, String mensagem) throws SQLException {


        //ve se palavra pass e nome canal funciona
        Statement st = dbConn.createStatement();

        String sqlQuery = "SELECT * FROM canais" +
                " WHERE name = '" + nomeCanal + "';";          //obtem canal
        System.out.println(nomeCanal);
        //realiza o comando, devolve um "conjunto de resultados"
        ResultSet rs = st.executeQuery(sqlQuery);

        if (!rs.next()) {       //se não houver nada
            rs.close();
            st.close();
            return false;
        }

        String pass = rs.getString("password");
        rs.close();

        if (!pass.equals(password)) {
            return false;
        }

        //cria o canal
        sqlQuery = "INSERT INTO channelchat VALUES (0, 0,'" + nomeCanal + "','" + nomeUser + "',' " + mensagem + "')";

        st.executeUpdate(sqlQuery);
        st.close();
        return true;
    }

    public ArrayList<Mensagem> listaMensagensCanal(String nomeCanal, int numeroMensagensAListar) throws SQLException {

        Statement st = dbConn.createStatement();

        String sqlQuery = "SELECT data, autor, canal, conteudo FROM channelchat";          //select from
        sqlQuery += " WHERE canal = '" + nomeCanal + "'";           //select enviadas por user1
        sqlQuery += " ORDER BY STR_TO_DATE(data,\"%d-%m-%Y\") DESC ";                         //ordena novo-> velho

        //realiza o comando, devolve um "conjunto de resultados"
        ResultSet rs = st.executeQuery(sqlQuery);

        ArrayList<Mensagem> listaMensagens = new ArrayList<Mensagem>();
        int msgCount = 0;
        while (rs.next() && msgCount < numeroMensagensAListar)              //paras as mensagens encontradas, ate não haver mais ou não ser preciso mais
        {
            msgCount++;
            //obtem dados de mensagens
            String autor = rs.getString("autor");
            String receptor = rs.getString("canal");
            String contudo = rs.getString("conteudo");

            listaMensagens.add(new Mensagem(autor, receptor, contudo));
        }
        rs.close();
        st.close();

        return listaMensagens;

    }

    public boolean isOwnerCanal(String nomeCanal, String nomeOwner) throws SQLException {

        Statement st = dbConn.createStatement();

        String sqlQuery = "SELECT owner FROM canais";          //select from
        sqlQuery += " WHERE name = '" + nomeCanal + "'";           //select enviadas por user1

        //realiza o comando, devolve um "conjunto de resultados"
        ResultSet rs = st.executeQuery(sqlQuery);

        if (!rs.next())      //caso nao haja canais com esse nome
            return false;

        String o = rs.getNString("owner");      //caso haja, obtem nome do owner do canal com o nome indicado

        rs.close();
        st.close();

        if (o.equals(nomeOwner)) {                    //se o owner for quem enviou isto
            return true;                            //devolve true
        }
        return false;

    }

    public void eliminaCanal(String nomeCanal) throws SQLException {
        Statement st = dbConn.createStatement();
        String sqlQuery = "DELETE FROM canais";
        sqlQuery += " WHERE name = '" + nomeCanal + "'";
        //realiza o comando, devolve um "conjunto de resultados"
        int rs = st.executeUpdate(sqlQuery);

    }

    public void eliminachatCanal(String nomeCanal) throws SQLException {
        Statement st = dbConn.createStatement();
        String sqlQuery = "DELETE FROM channelchat";
        sqlQuery += " WHERE canal = '" + nomeCanal + "'";
        //realiza o comando, devolve um "conjunto de resultados"
        int rs = st.executeUpdate(sqlQuery);
    }

    public int NumeroMensagensCanal(String nome) throws SQLException {
        int r = 0;


        Statement st = dbConn.createStatement();

        String sqlQuery = "SELECT * FROM channelchat";          //select from
        sqlQuery += " WHERE canal = '" + nome + "'";           //select enviadas por user1

        //realiza o comando, devolve um "conjunto de resultados"
        ResultSet rs = st.executeQuery(sqlQuery);

        while (rs.next())
            r++;

        return r;


    }

    public int NumeroFicheirosCanal(String nome) throws SQLException {
        int r = 0;

        Statement st = dbConn.createStatement();

        String sqlQuery = "SELECT * FROM ficheiros";          //select from
        sqlQuery += " WHERE nameChannel = '" + nome + "'";           //select enviadas por user1

        //realiza o comando, devolve um "conjunto de resultados"
        ResultSet rs = st.executeQuery(sqlQuery);

        while (rs.next())
            r++;

        return r;
    }

    public int NumeroUsersCanal(String nome) throws SQLException {
        int r = 0;

        Statement st = dbConn.createStatement();

        String sqlQuery = "SELECT DISTINCT autor FROM channelchat";          //select from
        sqlQuery += " WHERE canal = '" + nome + "'";           //select enviadas por user1

        //realiza o comando, devolve um "conjunto de resultados"
        ResultSet rs = st.executeQuery(sqlQuery);

        while (rs.next())
            r++;

        return r;
    }

    public void mudarDescricaoCanal(String nome, String novaDescricao) throws SQLException {
        Statement st = dbConn.createStatement();
        String sqlQuery = "UPDATE canais";
        sqlQuery += " SET descricao = '" + novaDescricao + "'";
        sqlQuery += " WHERE name = '" + nome + "'";
        //realiza o comando, devolve um "conjunto de resultados"
        int rs = st.executeUpdate(sqlQuery);
    }

    public void mudarPassCanal(String nome, String novaPass) throws SQLException {
        Statement st = dbConn.createStatement();
        String sqlQuery = "UPDATE canais";
        sqlQuery += " SET password = '" + novaPass + "'";
        sqlQuery += " WHERE name = '" + nome + "'";
        //realiza o comando, devolve um "conjunto de resultados"
        int rs = st.executeUpdate(sqlQuery);
    }

    /*
    public static void main(String[] args) {
        try {
            ConnDB connDB = new ConnDB();
            Scanner scanner = new Scanner(System.in);
            boolean exit = false;

            while (!exit) {
                System.out.print("Command: ");
                String command = scanner.nextLine();
                String[] comParts = command.split(",");

                if (command.startsWith("select"))
                    connDB.listUsers(null);
                else if (command.startsWith("find"))
                    connDB.listUsers(comParts[1]);
                else if (command.startsWith("insert"))
                    connDB.insertUser(comParts[1], comParts[2]);
                else if (command.startsWith("update"))
                    connDB.updateUser(Integer.parseInt(comParts[1]), comParts[2], comParts[3]);
                else if (command.startsWith("delete"))
                    connDB.deleteUser(Integer.parseInt(comParts[1]));
                else
                    exit = true;
            }

            connDB.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

     */
}
