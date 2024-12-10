package telran.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TCPServer implements Runnable
{
    private final Protocol protocol;
    private final int port;
    private final ExecutorService executorService;

    public TCPServer(Protocol protocol,
                     int port,
                     int socketTimeout,
                     int idleConnectionTimeout,
                     int limitRequestsPerSecond,
                     int limitNonOkResponsesInRow)
    {
        this.protocol = protocol;
        this.port = port;

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
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on the port "+ port);
            while(!executorService.isShutdown()) {
                try {
                    Socket socket = serverSocket.accept();
                    socket.setSoTimeout(TCPServerSettings.getIdleConnectionMsTimeout());
                    var session = new TCPClientServerSession(protocol, socket);
                    executorService.submit(session);
                } catch (IOException e) {
                    if (executorService.isShutdown()) {
                        break;
                    }
                    System.out.println(e);
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public boolean shutdown()
    {
        System.out.println("Shutdown initiated...");
        boolean res = true;
        executorService.shutdown();
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
