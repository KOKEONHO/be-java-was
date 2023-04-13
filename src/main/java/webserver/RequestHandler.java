package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;

import config.AppConfig;
import controller.UserController;
import db.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import request.Request;
import request.RequestParser;
import response.Response;

public class RequestHandler implements Runnable {

    private final String USER_CREATE = "/user/create";
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private final Socket connection;
    private final AppConfig appConfig = new AppConfig();
    private final UserController userController = appConfig.makeUserController();
    private final RequestParser requestParser = new RequestParser();

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            StringBuilder requestHeaderBuilder = new StringBuilder();       // Request Header
            String requestLine = br.readLine();     // Request Line
            logger.debug(">> requestLine: {}", requestLine);
            String line = br.readLine();
            while (!line.equals("")) {
                requestHeaderBuilder.append(line).append("\n");
                line = br.readLine();
            }
            Map<String, String> headerMap = requestParser.parseRequestHeader(requestHeaderBuilder.toString());
            logger.debug(">> headerMap: {}", headerMap);

            String[] splitRequestLine = requestParser.parseRequestLine(requestLine);
            Request request = createNewRequest(splitRequestLine);

            logger.debug(">> Request Handler -> request method: {}, request uri: {}, request version: {}", request.getMethod(), request.getUri(), request.getVersion());

            if (request.getUri().startsWith(USER_CREATE)) {
                request.setUri(userController.saveUser(request.getUri()));
                logger.debug(">> Request Handler -> New User: {}", Database.findUserById("rhrjsgh97"));
            }

            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            DataOutputStream dos = new DataOutputStream(out);
            String accept = headerMap.get("Accept").split(",")[0];
            logger.debug(">> RequestHandler -> accept: {}", accept);
            logger.debug(">> RequestHandler -> request.getUri(): {}", request.getUri());
            byte[] body = null;
            if (accept.endsWith("*") || accept.endsWith("css")) {
                body = Files.readAllBytes(new File("src/main/resources/static" + request.getUri()).toPath());
            } else {
                body = Files.readAllBytes(new File("src/main/resources/templates" + request.getUri()).toPath());
            }
            sendResponse(dos, body, accept);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void sendResponse(DataOutputStream dataOutputStream, byte[] body, String accept) {
        Response response = new Response(dataOutputStream, body.length, accept);
        response.send200Header();
        response.sendBody(body);
    }

    private Request createNewRequest(String[] splitRequestLine) {
        return new Request(splitRequestLine[0], splitRequestLine[1], splitRequestLine[2]);
    }

}
