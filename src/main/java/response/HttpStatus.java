package response;

public enum HttpStatus {

    OK(200, "200 OK"),
    FOUND(302, "302 Found"),
    NOT_FOUND(404, "404 Not Found");

    private final int code;
    private final String message;

    HttpStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getResponseMessage() {
        return message;
    }
}
