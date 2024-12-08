package telran.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import static java.lang.System.err;

public class TCPClientServerSession implements Runnable
{
    Protocol protocol;
    Socket socket;
    private static final RateLimiter rate_limiter = new RateLimiter(100, 1, TimeUnit.SECONDS);

    public TCPClientServerSession(Protocol protocol, Socket socket)
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
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintStream out = new PrintStream(socket.getOutputStream())) {
            String request;
            while ((request = in.readLine()) != null) {
                if (rate_limiter.allowRequest()) {
                    String response = protocol.getResponseWithJSON(request);
                    out.println(response);
                } else {
                    System.out.println("Rate limit exceeded");
                    out.println("Rate limit exceeded");
                }
            }
        } catch (SocketTimeoutException e) {
            System.out.println("Socket timed out due to inactivity: " + e.getMessage());
            closeSocket();
        } catch (IOException e) {
            System.out.println("Error processing request: " + e.getMessage());
            err.println(e);
        } finally {
            closeSocket();
        }
    }

    private void closeSocket() {
        try {
            if (!socket.isClosed()) {
                socket.close();
                System.out.println("Socket closed successfully.");
            }
        } catch (IOException e) {
            System.out.println("Error closing socket: " + e.getMessage());
            e.printStackTrace();
        }
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


