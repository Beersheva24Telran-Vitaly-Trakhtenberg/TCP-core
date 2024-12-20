package telran.net;

import telran.view.InputOutput;
import telran.view.*;

import java.io.IOException;

public class Main
{
    static EchoClient echoClient = null;

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
                    Item.of("Exit", io -> {
                        try {
                            exit(io);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
            };
            Menu menu = new Menu("Echo Application", items);
            menu.perform(new StandardInputOutput());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void exit(InputOutput io) throws IOException
    {
        if (echoClient != null) {
            echoClient.close();
        }
        System.exit(0);
    }

    private static void startSession(InputOutput io) throws IOException
    {
        String host = io.readString("Enter host: ");
        int port = io.readNumberRange("Enter port: ", "Wrong port!", 3000, 5000).intValue();
        if (echoClient != null) {
            echoClient.close();
        }
        echoClient = new EchoClient(host, port);
        Menu menu = new Menu(
                "Run Session",
                Item.of("Enter string", Main::stringProcessing),
                Item.ofExit()
            );
        menu.perform(io);
    }

    private static void stringProcessing(InputOutput io)
    {
        try {
            String message = io.readString("Enter string: ");
            String response = echoClient.messageSendAndReceive(message);
            io.writeLine(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
