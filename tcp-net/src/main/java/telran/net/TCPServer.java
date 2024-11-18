package telran.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer implements Runnable
{
    Protocol protocol;
    int port;
    ServerSocket server_socket;

    public TCPServer(Protocol protocol, int port)
    {
        this.protocol = protocol;
        this.port = port;
    }

    /**
     * Runs this operation.
     */
    @Override
    public void run()
    {
        try {
            server_socket = new ServerSocket(port);
            System.out.println("Server started on port " + port);
            while (true) {
                this.accept().run();
            }
        } catch (Exception e) {
            System.out.println("Error on the server: " + e.getMessage());
            System.err.println(e);
        }
    }

    public TCPClientServiceSession accept() throws IOException
    {
        Socket socket = server_socket.accept();
        return new TCPClientServiceSession(protocol, socket);
    }
}
