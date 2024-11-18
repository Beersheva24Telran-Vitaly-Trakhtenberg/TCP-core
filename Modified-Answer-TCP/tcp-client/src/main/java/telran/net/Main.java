package telran.net;

import telran.net.exceptions.ServerUnavailableException;
import telran.view.InputOutput;
import telran.view.*;

import java.io.IOException;

public class Main
{
    static TCPClient client = null;
    private static int port = 0;
    private static final String host = "localhost";

    public static void main(String[] args)
    {
        try {
            Item[] items = {
                    Item.of("Start session", io -> {
                        try {
                            startSession(io);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }),
                    Item.of("Exit", _ -> {
                        try {
                            exit();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } catch (ServerUnavailableException e) {
                            System.out.println(e.getMessage());
                            System.exit(0);
                        }
                    })
            };
            Menu menu = new Menu("Echo Application", items);
            menu.perform(new StandardInputOutput());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void exit() throws IOException, ServerUnavailableException
    {
        if (client !=null) {
            try {
                client.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (Exception e) {
                throw new ServerUnavailableException(host, port);
            }
        }
        System.exit(0);
    }

    private static void startSession(InputOutput io) throws IOException
    {
        //String host = io.readString("Enter host: ");
        port = io.readNumberRange("Enter port: ", "Wrong port!", 3000, 5000).intValue();
        if (client != null) {
            client.close();
        }
        client = new TCPClient(host, port);
        Menu menu = new Menu(
                "Run Session",
                Item.of("Echo: Enter string", Main::requestEcho),
                Item.of("Calculate Length: Enter string", Main::requestLength),
                Item.of("Reverse: Enter string", Main::requestReverse),
                Item.ofExit()
            );
        menu.perform(io);
    }

    private static void requestReverse(InputOutput io)
    {
        stringProcessing("reverse", io);
    }

    private static void requestLength(InputOutput io)
    {
        stringProcessing("length", io);
    }

    private static void requestEcho(InputOutput io)
    {
        stringProcessing("echo", io);
    }

    private static void stringProcessing(String request_type, InputOutput io)
    {
        try {
            String message = io.readString("Enter string: ");
            String response = client.processSendAndReceive(request_type, message);
            io.writeLine(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
