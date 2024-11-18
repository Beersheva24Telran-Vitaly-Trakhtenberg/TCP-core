package telran.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.*;

public class TCPClientServiceSession implements Runnable
{
    Protocol protocol;
    Socket socket;
    PrintStream out;
    BufferedReader in;

    public TCPClientServiceSession(Protocol protocol, Socket socket)
    {
        this.protocol = protocol;
        this.socket = socket;
    }
    /**
     * Runs this operation.
     */
    @Override
    public void run()
    {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintStream(socket.getOutputStream());
            String request = null;
            while ((request = in.readLine()) != null) {
                String response = protocol.getResponseWithJSON(request);
                out.println(response);
            }
            socket.close();
        } catch (Exception e) {
            System.out.println("Error processing request: " + e.getMessage());
            System.err.println(e);
        }
    }

    public BufferedReader getInputStream() throws IOException
    {
        return in;
    }

    public PrintStream getOutputStream() throws IOException
    {
        return out;
    }
}
