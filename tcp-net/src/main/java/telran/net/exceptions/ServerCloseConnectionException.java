package telran.net.exceptions;

public class ServerCloseConnectionException extends IllegalStateException
{
    public ServerCloseConnectionException(String host, int port)
    {
        super(String.format("Server %s:%d is unavailable", host, port));
    }
}
