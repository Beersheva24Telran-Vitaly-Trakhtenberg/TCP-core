package telran.net;

import java.net.*;
import java.io.*;

public class Main
{
    private static final int PORT = 3000;

    public static void main(String[] args) throws Exception
    {
        ServerSocket serverSocket = new ServerSocket(PORT);
        while(true) {
            Socket socket = serverSocket.accept();
            runSession(socket);
        }
    }

    private static void runSession(Socket socket) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintStream out = new PrintStream(socket.getOutputStream())) {
            String line = "";
            while((line = in.readLine()) != null) {
                out.printf("Echo Server on %s, port %d sends back %s\n", socket.getLocalAddress().getHostAddress(),
                        socket.getLocalPort(), line);
                System.out.println(line);
            }
        } catch (Exception e) {
            System.out.println("Client closed connection abnormally");
        }
    }
}
