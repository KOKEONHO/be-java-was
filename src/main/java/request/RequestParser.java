package request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RequestParser {

    private static final Logger logger = LoggerFactory.getLogger(RequestParser.class);
    private final String WELCOME_PAGE = "/index.html";

    public String[] parseRequestLine(String line) {
        String[] splitRequestLine = line.split(" ");
        logger.debug(">> RequestParser -> splitRequestLine: {}", Arrays.toString(splitRequestLine));
        // 만약 localhost:8080만 입력됐을 시 -> WELCOME PAGE
        if (splitRequestLine[1].equals("/")) {
            splitRequestLine[1] = WELCOME_PAGE;
        }
        return splitRequestLine;
    }

    public Map<String, String> parseRequestHeader(String requestHeader) {
        logger.debug(">> RequestParser -> String requestHeader: {}", requestHeader);
        String[] splitRequestHeader = requestHeader.split("\n");
        logger.debug("RequestParser -> splitRequestHeader: {}", Arrays.toString(splitRequestHeader));
        Map<String, String> requestHeaderMap = new HashMap<>();
        for (String string : splitRequestHeader) {
            String[] headerKeyValue = string.split(":", 2);
            requestHeaderMap.put(headerKeyValue[0], headerKeyValue[1].trim());
        }
        return requestHeaderMap;
    }

}
