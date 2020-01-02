package com.example.mongodb.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by dengzhiming on 2019/7/10
 */
@WebServlet(urlPatterns = "/async", asyncSupported = true)
public class AsyncServlet extends HttpServlet {
    private static final long serialVersionUID = 393375716683413545L;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        long start = System.currentTimeMillis();
        AsyncContext asyncContext = req.startAsync();
        CompletableFuture.runAsync(() -> execute(
                asyncContext,
                asyncContext.getRequest(),
                asyncContext.getResponse())
        );
        logger.info("总耗时: " + (System.currentTimeMillis() - start) + "ms");
    }

    private void execute(AsyncContext context, ServletRequest request,
                         ServletResponse response) {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            response.getWriter().append("hello");
        } catch (IOException e) {
            e.printStackTrace();
        }
        context.complete();
    }
}