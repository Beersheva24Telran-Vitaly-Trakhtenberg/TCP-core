package telran.net;

public enum ResponseCode
{
    SUCCESS(200),
    NOT_FOUND(404),
    WRONG_REQUEST(405), // Method Not Allowed
    WRONG_DATA(422),    // Unprocessable Entity
    INTERNAL_ERROR(500);

    private final int code;

    ResponseCode(int code)
    {
        this.code = code;
    }
}
