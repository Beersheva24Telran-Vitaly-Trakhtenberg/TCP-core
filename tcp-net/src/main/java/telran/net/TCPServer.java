package telran.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TCPServer implements Runnable
{
    private Protocol protocol;
    private int port;
    private final ExecutorService executorService;
    private static final int MAX_CONNECTIONS = 10;
    private static final int IDLE_CONNECTON_MS_TIMEOUT = 30000;

    public TCPServer(Protocol protocol, int port)
    {
        this.protocol = protocol;
        this.port = port;
        this.executorService = Executors.newFixedThreadPool(MAX_CONNECTIONS);

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
                    socket.setSoTimeout(IDLE_CONNECTON_MS_TIMEOUT);
                    var session = new TCPClientServerSession(protocol, socket);
                    executorService.submit(session);
/*
                } catch (SocketTimeoutException e) {
                    //ignore
*/
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

    public void shutdown()
    {
        System.out.println("Shutdown initiated...");
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
        System.out.println("Shutdown complete.");
    }

}
