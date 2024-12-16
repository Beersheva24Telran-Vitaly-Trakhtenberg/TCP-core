package telran.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TCPServer implements Runnable
{
    private final Protocol protocol;
    private final int port;
    private final ExecutorService executor_service;
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

        this.executor_service = Executors.newFixedThreadPool(TCPServerSettings.getMaxConnectionsNumber());
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
            while(running) {
                try {
                    Socket socket = server_socket.accept();
                    socket.setSoTimeout(TCPServerSettings.getIdleConnectionMsTimeout());
                    TCPClientServerSession session = new TCPClientServerSession(protocol, socket);
                    sessions.add(session);
                    executor_service.submit(session);
                } catch (IOException e) {
                    if (!running) {
                        break;
                    }
                    if (!(e instanceof java.net.SocketTimeoutException)) {
                        System.out.println(e);
                    }
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

    public boolean shutdown() throws IOException, InterruptedException
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

        executor_service.shutdown();

        try {
            if (!executor_service.awaitTermination((long) (TCPServerSettings.getIdleConnectionTimeout() * 1.05), TimeUnit.MILLISECONDS)) {
                executor_service.shutdownNow();
            }

            for (TCPClientServerSession session : sessions) {
                while (!session.isFinished()) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw e;
                    }
                }
            }
        } catch (InterruptedException e) {
            executor_service.shutdownNow();
            res = false;
        }
        System.out.println("Server shutdown complete.");

        return res;
    }
}
