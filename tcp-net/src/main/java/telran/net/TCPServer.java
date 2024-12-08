package telran.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer implements Runnable
{
    private Protocol protocol;
    private int port;
//    ServerSocket server_socket;

    public TCPServer(Protocol protocol, int port)
    {
        this.protocol = protocol;
        this.port = port;
    }

    /**
     * Runs this operation.
     */
    @Override
    public void run() {
        //TODO add SocketTimeOut handling for shutdown
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on the port "+ port);
            while(true) {
                Socket socket = serverSocket.accept();
                var session = new TCPClientServerSession(protocol, socket);
                Thread thread = new Thread(session);

                thread.start();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void shutdown() {
        //TODO
        //In the ExecutorService framework to provide shutdownNow (to ignore all not processing client sessions)
    }

}
