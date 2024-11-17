package telran.net;

import static telran.net.ResponseCode.*;

public class Main
{
    private static final Protocol PROTOCOL = request -> {
        Response response;
        if (request.requestType() != null) {
            response = switch (request.requestType().toLowerCase()) {
                case "echo" -> Controller.messageEcho(request.requestData());
                case "length" -> Controller.calculateLength(request.requestData());
                case "reverse" -> Controller.messageReverse(request.requestData());
                default -> new Response(WRONG_REQUEST, String.format("Wrong request type: %s", request.requestType()));
            };
        } else {
            response = new Response(WRONG_DATA, "Wrong request data, null given");
        }
        return response;
    };
    private static final int PORT = 3000;

    public static void main(String[] args) throws Exception
    {
        while(true) {
            try {
                TCPServer server = new TCPServer(PROTOCOL, PORT);
                server.run();
            } catch (Exception e) {
                System.out.println("Client closed connection abnormally");
                System.err.println(String.format("%s \n %s", e.getMessage(), e.getStackTrace().toString()));
            }
        }
    }

}
