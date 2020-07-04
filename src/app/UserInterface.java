package app;

import java.io.*;
import java.lang.reflect.Array;
import java.net.SocketException;

public class UserInterface {
    protected InputStream in;
    protected OutputStream out;

    public UserInterface(InputStream reader, OutputStream writer) {
        this.in = reader;
        this.out = writer;
    }

    public Object receive() throws IOException, ClassNotFoundException, SocketException {
        ObjectInputStream ois = new ObjectInputStream(in);
        Request request = (Request) ois.readObject();
        Object o = new Serialization().DeserializeObject(request.getContent());
        return o;
    }

    public void send(Object o) throws IOException {
        Request request = new Request(new Serialization().SerializeObject(o));
        byte msg[] = new Serialization().SerializeObject(request);
        out.write(msg);
    }

    public void send(String str, Object o) throws IOException {
        byte SerStr[] = new Serialization().SerializeObject(str + " ");
        byte SerO[] = new Serialization().SerializeObject(o);
        byte SerBoth[] = concat(SerStr, SerO);
        Request request = new Request(SerBoth);
        byte msg[] = new Serialization().SerializeObject(request);
        out.write(msg);
    }

    byte[] concat(byte[] A, byte[] B) {
        int aLen = A.length;
        int bLen = B.length;
        byte[] C= new byte[aLen+bLen];
        System.arraycopy(A, 0, C, 0, aLen);
        System.arraycopy(B, 0, C, aLen, bLen);
        return C;
    }

    public Object request(String msg) throws IOException, ClassNotFoundException {
        send(msg);
        return receive();
    }
}
