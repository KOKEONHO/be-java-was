package response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.RequestHandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

public class Response {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private final String ACCEPT = "Accept";
    private final String httpMethod;
    private final String httpVersion;
    private final String requestTarget;
    private final String accept;
    private byte[] body;
    private String startLine;
    private String header;

    public Response(String httpMethod, String requestTarget, String httpVersion, Map<String, String> requestHeaderMap) throws IOException {
        this.httpMethod = httpMethod;
        this.requestTarget = requestTarget;
        this.httpVersion = httpVersion;
        this.accept = getAcceptFromHeaderMap(requestHeaderMap);
        setResponseBody();
        logger.debug(">> Response -> Response() -> requestTarget: {}", requestTarget);
        logger.debug(">> Response -> Response() -> accept: {}", accept);
        setResponseStartLine();
        setResponseHeader();
    }

    public String getAcceptFromHeaderMap(Map<String, String> requestHeaderMap) {
        return requestHeaderMap.get(ACCEPT).split(",")[0];
    }

    public void setResponseStartLine() {
        StringBuilder startLineBuilder = new StringBuilder();
        startLineBuilder.append(httpVersion + " " + HttpStatus.OK + " \r\n");
        startLine = startLineBuilder.toString();
    }

    public void setResponseBody() throws IOException {
        if (accept.endsWith("*") || accept.endsWith("css")) {
            body = Files.readAllBytes(new File("src/main/resources/static/" + requestTarget).toPath());
            return;
        }
        body = Files.readAllBytes(new File("src/main/resources/templates/" + requestTarget).toPath());
    }

    public void setResponseHeader() {
        StringBuilder headerBuilder = new StringBuilder();
        headerBuilder.append("Content-Type: " + accept + ";charset=UTF-8\r\n");
        headerBuilder.append("Content-Length: " + body.length + "\r\n\r\n");
        header = headerBuilder.toString();
    }

    public String getStartLine() {
        return startLine;
    }

    public String getHeader() {
        return header;
    }

    public byte[] getBody() {
        return body;
    }

}
