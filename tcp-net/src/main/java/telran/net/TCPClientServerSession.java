package telran.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class TCPClientServerSession implements Runnable
{
    private final Protocol protocol;
    private final Socket socket;
    private static final RateLimiter rate_limiter = new RateLimiter(100, 1, TimeUnit.SECONDS);
    private volatile boolean running = true;
    private volatile boolean finished = false;
    private final ReentrantLock lock = new ReentrantLock();

    public TCPClientServerSession(Protocol protocol, Socket socket) throws SocketException
    {
        this.protocol = protocol;
        this.socket = socket;
        this.socket.setSoTimeout(TCPServerSettings.getSocketTimeout());
    }

    /**
     * Runs this operation.
     */
    @Override
    public void run()
    {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintStream out = new PrintStream(socket.getOutputStream())) {

            String request;
            while(running) {
                try {
                    if (socket.isClosed()) {
                        break;
                    }

                    if ((request = in.readLine()) != null) {
                        if (rate_limiter.allowRequest()) {
                            String response = protocol.getResponseWithJSON(request);
                            out.println(response);
                        } else {
                            System.out.println("Rate limit exceeded");
                            out.println("Rate limit exceeded");
                        }
                    } else {
                        break;
                    }
                } catch (SocketTimeoutException e) {
                    System.out.println("Session timed out due to inactivity: " + e.getMessage());
                    shutdownNow();
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Error processing request: " + e.getMessage());
            System.err.println(e);
            e.printStackTrace();
        } finally {
            finished = true;
        }
    }

    private void closeSocket() {
        try {
            if (!socket.isClosed()) {
                socket.close();
                finished = true;
                System.out.println("Session closed successfully.");
            }
        } catch (IOException e) {
            System.out.println("Error closing session: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void shutdown() throws IOException
    {
        System.out.println("Session shutdown initiated...");
        lock.lock();
        try {
            running = false;
        } finally {
            lock.unlock();
        }
        System.out.println("Session shutdown completed...");
    }

    public void shutdownNow() throws IOException
    {
        System.out.println("Session shutdownNow initiated...");
        lock.lock();
        try {
            running = false;
            closeSocket();
        } finally {
            lock.unlock();
        }
        System.out.println("Session shutdownNow completed...");
    }

    public boolean isFinished()
    {
        return finished;
    }

    private static class RateLimiter
    {
        private final long max_requests;
        private final long time_window;
        private long request_count;
        private long start_time;

        public RateLimiter(long max_requests, long time_window, TimeUnit time_unit)
        {
            this.max_requests = max_requests;
            this.time_window = time_unit.toMillis(time_window);
            this.request_count = 0;
            this.start_time = System.currentTimeMillis();
        }

        public synchronized boolean allowRequest()
        {
            long current_time = System.currentTimeMillis();
            boolean res = false;
            if (current_time - start_time > time_window) {
                start_time = current_time;
                request_count = 0;
            }
            if (request_count < max_requests) {
                request_count++;
                res = true;
            }
            return res;
        }
    }
}


