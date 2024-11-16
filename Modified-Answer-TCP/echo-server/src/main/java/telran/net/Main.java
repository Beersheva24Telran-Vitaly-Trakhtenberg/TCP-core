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
                String[] tmp_arr = line.split("#");
                if (tmp_arr.length > 1) {
                    if (tmp_arr[0].equals("R")) {
                        line = input_reverse(tmp_arr[1]);
                    } else if (tmp_arr[0].equals("L")) {
                        line = input_length(tmp_arr[1]);
                    } else {
                        line = "Modificator '" + tmp_arr[0] + "' Undefined!";
                    }
                }
                out.printf("Echo Server on %s, port %d sends back: '%s'\n", socket.getLocalAddress().getHostAddress(),
                        socket.getLocalPort(), line);
                System.out.println(line);
            }
        } catch (Exception e) {
            System.out.println("Client closed connection abnormally");
        }
    }

    private static String input_length(String s)
    {
        return String.format("Length of input '%s' is %d",s,s.length());
    }

    private static String input_reverse(String s)
    {
        int max = s.length();
        String res = "";
        for (int i=max-1; i>=0; i--) {
            String symb = s.substring(i, i+1);
            res = res.concat(symb);
        }
        return res;
    }
}
