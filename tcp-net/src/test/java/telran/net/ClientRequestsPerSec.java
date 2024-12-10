package telran.net;

import telran.net.exceptions.ServerUnavailableException;

import java.io.IOException;

public class ClientRequestsPerSec
{
    public static void main(String[] args) throws InterruptedException, IOException {
        TCPClient tcpClient = new TCPClient("localhost", 5000);
        for(int i = 0; i < 10; i++) {
            try {
                tcpClient.sendAndReceive("ok", "");
                System.out.println("Request " + i);
            } catch (ServerUnavailableException e) {
                System.out.println("Server closed connection");
                break;
            }
        }
        try {
            tcpClient.close();
        } catch (Exception e) {

        }
    }
}
