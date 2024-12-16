package telran.net;

import telran.net.exceptions.ServerUnavailableException;

import java.io.IOException;

public class ClientNonOkRequests
{
    public static void main(String[] args) throws InterruptedException, IOException
    {
        TCPClient tcpClient = new TCPClient("localhost", 5000);
        for(int i = 0; i < 15; i++) {
            try {
                Thread.sleep(2000);
                tcpClient.sendAndReceive("kuku", "");


            } catch(ServerUnavailableException e) {
                System.out.println("Server closed connection");
                break;
            }
            catch (RuntimeException e) {
                System.out.println("Request " + i);
            }
        }
        try {
            tcpClient.close();
        } catch (Exception e) {

        }
    }
}