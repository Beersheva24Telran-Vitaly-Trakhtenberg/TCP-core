package telran.net;

public class Controller
{
    public static Response messageEcho(String message)
    {
        return new Response(ResponseCode.SUCCESS, message);
    }

    public static Response calculateLength(String message)
    {
        return new Response(ResponseCode.SUCCESS, String.format("Length of input '%s' is %d",message,message.length()));
    }

    public static Response messageReverse(String message)
    {
        int max = message.length();
        String res = "";
        for (int i=max-1; i>=0; i--) {
            String symb = message.substring(i, i+1);
            res = res.concat(symb);
        }

        return new Response(ResponseCode.SUCCESS, String.format("Reversed value is %s",res));
    }
}
