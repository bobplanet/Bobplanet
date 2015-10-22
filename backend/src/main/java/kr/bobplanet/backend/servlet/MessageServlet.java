package kr.bobplanet.backend.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.bobplanet.backend.api.MessageEndpoint;

/**
 *
 *
 * @author heonkyu.jin
 * @version 15. 10. 22
 */
public class MessageServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);

        MessageEndpoint endpoint = new MessageEndpoint();
        endpoint.sendNextMenuMessage();
    }
}
