package telran.net;

import org.json.JSONObject;
import telran.net.exceptions.*;

import java.io.*;
import java.net.Socket;
import java.time.Instant;

import static telran.net.TCPConfigProperties.*;

public class TCPClient implements Closeable, NetworkClient
{
    private Socket socket;
    private PrintStream writer;
    private BufferedReader reader;
    private int interval;
    private int number_trials;
    private String host;
    private int port;
    
    public TCPClient(String host, int port, int interval, int number_trials) throws IOException
    {
        this.host = host;
        this.port = port;
        this.interval = interval;
        this.number_trials = number_trials;
        connect();
    }

    public TCPClient(String host, int port) throws IOException
    {
        this(host, port, DEFAULT_INTERVAL_CONNECTION, DEFAULT_NUMBER_TRIALS_CONNECTIONS);
    }

    private void connect() throws IOException
    {
        int counter = number_trials;
        do {
            try {
                socket = new Socket(host, port);
                writer = new PrintStream(socket.getOutputStream());
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                break;
            } catch (IOException e) {
                waitForInterval();
                counter--;
            }
        } while (counter != 0);
        if (this.socket == null) {
            //connect();
            throw new ServerUnavailableException(host, port);
        }
    }

    private void waitForInterval()
    {
        Instant finish = Instant.now().plusMillis(interval);
        while (Instant.now().isBefore(finish));
    }

    @Override
    public void close() throws IOException {
        if(socket != null) {
            socket.close();
        }
    }

    @Override
    public String sendAndReceive(String requestType, String requestData) throws IOException
    {
        Request request = new Request(requestType, requestData);

        try {
            if(socket == null) {
                throw new ServerUnavailableException(host, port);
            }
            writer.println(request);
            String responseJSON = reader.readLine();
            JSONObject jsonObj = new JSONObject(responseJSON);
            ResponseCode responseCode = jsonObj.getEnum(ResponseCode.class, RESPONSE_CODE_FIELD);
            String responseData = jsonObj.getString(RESPONSE_DATA_FIELD);
            if(responseCode != ResponseCode.SUCCESS) {
                throw new RuntimeException(responseData);
            }
            return responseData;
        } catch (IOException e) {
            connect();
            throw new ServerUnavailableException(host, port);
        }
    }
}
