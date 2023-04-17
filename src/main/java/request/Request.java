package request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.RequestHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;

public class Request {

    // HTTP 요청을 나타내는 클래스
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private final String requestLine;
    private StringBuilder requestHeaderBuilder;
    private Map<String, String> requestHeaderMap;
    private String httpMethod;
    private String requestTarget;
    private String httpVersion;

    public Request(BufferedReader bufferedReader, RequestParser requestParser) throws IOException {     // 생성자
        requestLine = bufferedReader.readLine();
        parseRequestLine(requestParser);
        logger.debug(">> Request -> Request() -> requestLine: {}", requestLine);
        String headerLine = bufferedReader.readLine();
//        if (headerLine == null) {
//            return;
//        }
        requestHeaderBuilder = new StringBuilder();
        while (!headerLine.equals("")) {
            requestHeaderBuilder.append(headerLine).append("\n");
            headerLine = bufferedReader.readLine();
        }
        logger.debug(">> Request -> httpMethod: {}, requestTarget: {}, httpVersion: {}", httpMethod, requestTarget, httpVersion);
        parseRequestHeader(requestParser);
    }

    public void parseRequestLine(RequestParser requestParser) {
        String[] parsedRequestLine = requestParser.parseRequestLine(requestLine);
        logger.debug(">> Request -> parseRequestLine() -> parsedRequestLine: {}", Arrays.toString(parsedRequestLine));
        httpMethod = parsedRequestLine[0];
        requestTarget = parsedRequestLine[1];
        httpVersion = parsedRequestLine[2];
    }

    public void parseRequestHeader(RequestParser requestParser) {
        String requestHeader = requestHeaderBuilder.toString();
        requestHeaderMap = requestParser.parseRequestHeader(requestHeader);
        logger.debug(">> Request -> parseRequestHeader() -> requestHeaderMap: {}", requestHeaderMap);
    }

    public Map<String, String> getRequestHeaderMap() {
        return requestHeaderMap;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getRequestTarget() {
        return requestTarget;
    }

    public String getHttpVersion() {
        return httpVersion;
    }
}
