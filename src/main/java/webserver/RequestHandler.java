package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import config.AppConfig;
import controller.UserController;
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

        try (InputStream inputStream = connection.getInputStream(); OutputStream outputStream = connection.getOutputStream()) {
            Request request = createRequest(inputStream, requestParser);
            Response response = createResponse(request);
            doResponse(outputStream, response);
//            if (request.getUri().startsWith(USER_CREATE)) {
//                request.setUri(userController.saveUser(request.getUri()));
//                logger.debug(">> Request Handler -> New User: {}", Database.findUserById("rhrjsgh97"));
//            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private Request createRequest(InputStream inputStream, RequestParser requestParser) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        Request request = new Request(bufferedReader, requestParser);
        return request;
    }

    private Response createResponse(Request request) throws IOException {
        Response response = new Response(request.getHttpMethod(), request.getRequestTarget(), request.getHttpVersion(), request.getRequestHeaderMap());
        return response;
    }

    private void doResponse(OutputStream outputStream, Response response) {
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        try {
            dataOutputStream.writeBytes(response.getStartLine());
            dataOutputStream.writeBytes(response.getHeader());
            dataOutputStream.write(response.getBody(), 0, response.getBody().length);
            dataOutputStream.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

}
