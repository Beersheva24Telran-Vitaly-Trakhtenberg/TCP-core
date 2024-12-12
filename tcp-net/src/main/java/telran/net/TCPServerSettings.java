package telran.net;

public class TCPServerSettings
{
    private static int MAX_CONNECTIONS_NUMBER = 10;
    private static int IDLE_CONNECTON_MS_TIMEOUT = 30000;
    private static int DEFAULT_SOCKET_TIMEOUT = 30000;
    private static int DEFAULT_IDLE_CONNECTION_TIMEOUT = 60000;
    private static int DEFAULT_LIMIT_REQUESTS_PER_SEC = 5;
    private static int DEFAULT_LIMIT_NON_OK_RESPONSES_IN_ROW = 10;

    public static int getMaxConnectionsNumber()
    {
        return MAX_CONNECTIONS_NUMBER;
    }

    public static int getIdleConnectionMsTimeout()
    {
        return IDLE_CONNECTON_MS_TIMEOUT;
    }

    public static int getSocketTimeout()
    {
        return DEFAULT_SOCKET_TIMEOUT;
    }

    public static int getIdleConnectionTimeout()
    {
        return DEFAULT_IDLE_CONNECTION_TIMEOUT;
    }

    public static int getLimitRequestsPerSecond()
    {
        return DEFAULT_LIMIT_REQUESTS_PER_SEC;
    }

    public static int getLimitNonOkResponsesInRow()
    {
        return DEFAULT_LIMIT_NON_OK_RESPONSES_IN_ROW;
    }

    public static void setMaxConnectionsNumber(int max_connections_number)
    {
        MAX_CONNECTIONS_NUMBER = max_connections_number;
    }

    public static void setIdleConnectionMsTimeout(int idle_connecton_ms_timeout)
    {
        IDLE_CONNECTON_MS_TIMEOUT = idle_connecton_ms_timeout;
    }

    public static void setSocketTimeout(int socket_timeout)
    {
        DEFAULT_SOCKET_TIMEOUT = socket_timeout;
    }

    public static void setIdleConnectionTimeout(int idle_connection_timeout)
    {
        DEFAULT_IDLE_CONNECTION_TIMEOUT = idle_connection_timeout;
    }

    public static void setLimitRequestsPerSecond(int limit_requests_per_second)
    {
        DEFAULT_LIMIT_REQUESTS_PER_SEC = limit_requests_per_second;
    }

    public static void setLimitNonOkResponsesInRow(int limit_non_ok_responses_in_row)
    {
        DEFAULT_LIMIT_NON_OK_RESPONSES_IN_ROW = limit_non_ok_responses_in_row;
    }
}
