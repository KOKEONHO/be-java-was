package controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import request.RequestParser;
import service.UserSignUpService;

public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(RequestParser.class);

    private final UserSignUpService userSignUpService;

    public UserController(UserSignUpService userSignUpService) {
        this.userSignUpService = userSignUpService;
    }

    private String getQueryString(String uri) {
        String[] splittedUri = uri.split("\\?");
        return splittedUri[1];
    }

    public String saveUser(String uri) {
        String queryString = getQueryString(uri);
        logger.debug(">> UserController -> queryString: {}", queryString);
        userSignUpService.userSignUp(queryString);
        return "/index.html";
    }
}
