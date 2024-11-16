package telran.net;

import java.io.*;

import static telran.net.ResponseCode.*;

public class Main
{
    private static final Protocol PROTOCOL = new Protocol()
    {
        @Override
        public Response getResponse(Request request) {
            Response response = new Response(SUCCESS, "OK");
            return response;
        }
    };
    private static final int PORT = 3000;

    public static void main(String[] args) throws Exception
    {
        TCPServer server = new TCPServer(PROTOCOL, PORT);
        server.run();
        while(true) {
            TCPClientServiceSession session = server.accept();
            runSession(session);
        }
    }

    private static void runSession(TCPClientServiceSession session) throws IOException
    {
        try (BufferedReader in = new BufferedReader(session.getInputStream());
        PrintStream out = new PrintStream(session.getOutputStream())) {
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
                out.printf("Echo Server sends back: '%s'\n", line);
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
