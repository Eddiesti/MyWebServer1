package ru.otus.hibernate.servlets;

import org.hibernate.ObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.otus.hibernate.cache.CacheEngine;
import ru.otus.hibernate.cache.CacheEngineImpl;
import ru.otus.hibernate.cache.MyElement;
import ru.otus.hibernate.entity.UserDataSet;
import ru.otus.hibernate.service.DBServiceHibernateImpl;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DataInfoPageServlet extends HttpServlet {
    private ApplicationContext context = new ClassPathXmlApplicationContext("SpringBean.xml");
    private static Logger logger = LoggerFactory.getLogger(AddUserServlet.class);
    @Autowired
    private DBServiceHibernateImpl service = context.getBean("service", DBServiceHibernateImpl.class);
    private CacheEngine cache = service.getCache();

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        Map<String, Object> pageVariables = new HashMap<>();

        TemplateProcessor templateProcessor = (TemplateProcessor) getServletContext().getAttribute("templateProcessor");

        pageVariables.put("count", service.getCountUsers());

        String idString = request.getParameter("id");
        try {
            if (idString != null && !idString.isEmpty()) {
                String userById;
                long id = Long.parseLong(idString);
                userById = service.getUserById(id, UserDataSet.class);
                if (cache.get(id) != null) {
                    pageVariables.put("name", service.getCache().get(id).toString());
                } else {
                    cache.put(new MyElement(id,userById));
                    pageVariables.put("name",service.getCache().get(id).toString());
                }
            }
        } catch (ObjectNotFoundException ex) {
            logger.info("User not found");
            response.sendRedirect("/error.html");
            return;
        }
        String pageText = templateProcessor.getPage("data_info.html", pageVariables);
        response.getWriter().println(pageText);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) {

    }
}
