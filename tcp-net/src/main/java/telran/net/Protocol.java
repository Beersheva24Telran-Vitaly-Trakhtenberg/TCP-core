package telran.net;

import org.json.JSONObject;

import static telran.net.TCPConfigProperties.*;

public interface Protocol
{
    Response getResponse(Request request);
    default String getResponseWithJSON(String requestJSON)
    {
        JSONObject jsonObj = new JSONObject(requestJSON);
        String requestType = jsonObj.getString(REQUEST_TYPE_FIELD);
        String requestData = jsonObj.getString(REQUEST_DATA_FIELD);
        Request request = new Request(requestType, requestData);
        Response response = getResponse(request);
        return response.toString();
    }
}
