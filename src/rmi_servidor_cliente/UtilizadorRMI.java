package rmi_servidor_cliente;

import java.io.*;

public class UtilizadorRMI implements Serializable {

    static final long serialVersionUID = 42L;

    private String nome;
    private String password;
    private byte[] fotoLocation;

    public UtilizadorRMI(String nome, String password, String fotoLocation) throws IOException {
        this.nome = nome;
        this.password = password;

        if (fotoLocation != null)            //caso nao seja null, logo vai registar, logo precisa de carregar a foto
        {
            File file = new File(fotoLocation);


            FileInputStream fileInputStream=new FileInputStream(file);
            this.fotoLocation=new byte[(int) file.length()];
            BufferedInputStream bufferedInputStream=new BufferedInputStream(fileInputStream);
            bufferedInputStream.read(this.fotoLocation,0, this.fotoLocation.length);

        } else {
            this.fotoLocation = null;

        }
    }



    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public byte[] getFotoLocation() {
        return fotoLocation;
    }

    public void setFotoLocation(byte[] fotoLocation) {
        this.fotoLocation = fotoLocation;
    }

    //da thwors quando não consegue criar um bufere image
    //guarda imagem na pasta resouces
    //coloca a localização da imagem na var "pathName"-> bast passar para striong para ler
    public void guardaFoto() throws IOException {

        /*InputStream is = new ByteArrayInputStream(fotoLocation);
        BufferedImage bImage = ImageIO.read(is);
        if(bImage == null)
            throw  new IOException();

        String pathName = "src/resources/" + nome + ".png";
        File f = new File(pathName);
        ImageIO.write(bImage, "png", f);
        */
        String pathName = "src/resources/" + nome + ".png";
        try (FileOutputStream fos = new FileOutputStream(pathName)) {
            fos.write(fotoLocation);
            //fos.close(); There is no more need for this line since you had created the instance of "fos" inside the try. And this will automatically close the OutputStream
        }

        fotoLocation = pathName.getBytes();

    }
}
