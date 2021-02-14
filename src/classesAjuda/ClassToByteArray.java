package classesAjuda;

import java.io.*;

public final class ClassToByteArray {
    private ClassToByteArray(){};

    static ByteArrayOutputStream bos;
    static ObjectOutputStream out;

    //cuidado com o return do null
    public synchronized static <T extends Serializable> byte[] serialize( T objeto){
        byte[] yourBytes = null;
        bos = new ByteArrayOutputStream();
        out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(objeto);
            out.flush();
            yourBytes = bos.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                // ignorar
            }
        }

        return yourBytes;
    }

    //cuidado com o return do null
    public synchronized static Object unSerialize( byte[] array){
        Object o = null;
        ByteArrayInputStream bis = new ByteArrayInputStream(array);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            o = in.readObject();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                // ignorar
            }
        }
        return o;
    }


}
