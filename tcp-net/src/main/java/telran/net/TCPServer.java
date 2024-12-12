package telran.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.*;

public class TCPServer implements Runnable
{
    private final Protocol protocol;
    private final int port;
    private final ExecutorService executorService;
    private final List<TCPClientServerSession> sessions;
    private volatile boolean running = true;
    private ServerSocket server_socket;

    public TCPServer(Protocol protocol,
                     int port,
                     int socketTimeout,
                     int idleConnectionTimeout,
                     int limitRequestsPerSecond,
                     int limitNonOkResponsesInRow)
    {
        this.protocol = protocol;
        this.port = port;
        this.sessions = new CopyOnWriteArrayList<>();

        TCPServerSettings.setSocketTimeout(socketTimeout);
        TCPServerSettings.setIdleConnectionTimeout(idleConnectionTimeout);
        TCPServerSettings.setLimitNonOkResponsesInRow(limitNonOkResponsesInRow);
        TCPServerSettings.setLimitRequestsPerSecond(limitRequestsPerSecond);
        TCPServerSettings.setLimitNonOkResponsesInRow(limitNonOkResponsesInRow);

        this.executorService = Executors.newFixedThreadPool(TCPServerSettings.getMaxConnectionsNumber());
    }

    public TCPServer(Protocol protocol, int port, int idleConnectionTimeout)
    {
        this(protocol,
            port,
            TCPServerSettings.getSocketTimeout(),
            idleConnectionTimeout,
            TCPServerSettings.getLimitRequestsPerSecond(),
            TCPServerSettings.getLimitNonOkResponsesInRow());
    }

    public TCPServer(Protocol protocol, int port)
    {
        this(protocol,
            port,
                TCPServerSettings.getSocketTimeout(),
                TCPServerSettings.getIdleConnectionTimeout(),
                TCPServerSettings.getLimitRequestsPerSecond(),
                TCPServerSettings.getLimitNonOkResponsesInRow());
    }

    /**
     * Runs this operation.
     */
    @Override
    public void run()
    {
        try {
            server_socket = new ServerSocket(port);
            server_socket.setSoTimeout(TCPServerSettings.getSocketTimeout());
            System.out.println("Server is listening on the port "+ port);
            while(running) {    // !executorService.isShutdown()
                try {
                    Socket socket = server_socket.accept();
                    socket.setSoTimeout(TCPServerSettings.getIdleConnectionMsTimeout());
                    TCPClientServerSession session = new TCPClientServerSession(protocol, socket);
                    sessions.add(session);
                    executorService.submit(session);
                } catch (IOException e) {
                    if (!running) {
                        break;
                    }
                    System.out.println(e);
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            if (server_socket != null && !server_socket.isClosed()) {
                try {
                    server_socket.close();
                } catch (IOException e) {
                    System.out.println("Error closing server socket: " + e.getMessage());
                }
            }
        }
    }

    public boolean shutdown() throws IOException
    {
        System.out.println("Server shutdown initiated...");
        boolean res = true;
        running = false;
        if (server_socket != null && !server_socket.isClosed()) {
            try {
                server_socket.close();
            } catch (IOException e) {
                System.out.println("Error closing server socket: " + e.getMessage());
                res = false;
            }
        }
        executorService.shutdown();
        for (TCPClientServerSession session : sessions) {
            session.shutdown();
        }
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
                res = false;
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            res = false;
        }
        System.out.println("Shutdown complete.");

        return res;
    }

}
