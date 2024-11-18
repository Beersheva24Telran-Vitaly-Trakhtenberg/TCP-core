package telran.net.exceptions;

public class ServerUnavailableException  extends IllegalStateException
{
    public ServerUnavailableException(String host, int port)
    {
        super(String.format("Server %s:%d is unavailable", host, port));
    }
}
