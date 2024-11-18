package telran.net;

public interface TCPConfigProperties
{
    String REQUEST_TYPE_FIELD = "requestType";
    String REQUEST_DATA_FIELD = "requestData";
    String RESPONSE_CODE_FIELD = "responseCode";
    String RESPONSE_DATA_FIELD = "responseData";
    static final int DEFAULT_INTERVAL_CONNECTION = 10;
    static final int DEFAULT_NUMBER_TRIALS_CONNECTIONS = 50;
}
