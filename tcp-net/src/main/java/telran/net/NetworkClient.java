package telran.net;

import java.io.IOException;

public interface NetworkClient
{
    String sendAndReceive(String requestType, String requestData) throws IOException;
}
