package telran.net;

import java.io.IOException;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ApplicationServerTest
{
    public static void main(String[] args) throws IOException, InterruptedException {
        Protocol protocol = req -> {
            return switch(req.requestType()) {
                case "ok" -> new Response(ResponseCode.SUCCESS, "");
                default -> new Response(ResponseCode.WRONG_REQUEST, "");
            };
        };
        TCPServer server = new TCPServer(protocol, 5000, 10000);
        new Thread(server).start();
        Scanner scanner = new Scanner(System.in);
        boolean shutdowning = false;
        while(true) {
            System.out.println("Enter word 'shutdown' for stopping server");
            String line = scanner.nextLine();
            if(line.equals("shutdown")) {
                shutdowning = server.shutdown();
                break;
            }
        }
        assertTrue(shutdowning);
    }
}
