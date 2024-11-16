package telran.net;

import java.net.*;
import java.io.*;

public class EchoClient
{
    private Socket socket;
    private BufferedReader in;
    private PrintStream out;

    public EchoClient(String host, int port) throws IOException
    {
        try {
            socket = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintStream(socket.getOutputStream(), true);
        } catch (IOException e) {
            socket.close();
            throw e;
        } catch (Exception e) {
            socket.close();
            throw e;
        }
    }

    public String messageSendAndReceive(String message) throws IOException
    {
        try {
            out.println(message);
            return in.readLine();
        } catch (Exception e) {
            socket.close();
            throw new RuntimeException(e);
        }
    }

    public void close() throws IOException {
        try {
            socket.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
