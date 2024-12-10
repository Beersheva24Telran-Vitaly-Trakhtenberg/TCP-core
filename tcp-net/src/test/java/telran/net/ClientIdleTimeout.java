package telran.net;

import java.io.IOException;

public class ClientIdleTimeout
{
    public static void main(String[] args) throws InterruptedException, IOException
    {
        TCPClient tcpClient = new TCPClient("localhost", 5000);

        try{
            tcpClient.sendAndReceive("ok", "");
            Thread.sleep(60000);
            tcpClient.sendAndReceive("ok", "");
            System.out.println("request");

        } catch(RuntimeException | IOException e) {
            System.out.println("Server closed connection");

        }
    }
}
