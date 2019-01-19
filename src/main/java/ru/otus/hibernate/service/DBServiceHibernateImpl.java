package ru.otus.hibernate.service;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import ru.otus.hibernate.cache.CacheEngine;
import ru.otus.hibernate.cache.CacheEngineImpl;
import ru.otus.hibernate.cache.MyElement;
import ru.otus.hibernate.config.HibernateConfiguration;
import ru.otus.hibernate.dao.UsersDAO;
import ru.otus.hibernate.entity.DataSet;

public class DBServiceHibernateImpl implements DBService {
    private final SessionFactory sessionFactory;
    private ApplicationContext context = new ClassPathXmlApplicationContext("src\\main\\resources\\SpringBean.xml");

    private CacheEngine cache = context.getBean("cache",CacheEngineImpl.class);

    public DBServiceHibernateImpl() {
        sessionFactory = createSessionFactory(HibernateConfiguration.setup());
    }

    private static SessionFactory createSessionFactory(Configuration configuration) {
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
        builder.applySettings(configuration.getProperties());
        ServiceRegistry serviceRegistry = builder.build();
        return configuration.buildSessionFactory(serviceRegistry);
    }

    public CacheEngine getCache() {
        return cache;
    }

    public <T extends DataSet> void save(T user) {
        try (Session session = sessionFactory.openSession()) {
            UsersDAO usersDAO = new UsersDAO(session);
            cache.put(new MyElement(user.getId(), user));
            usersDAO.save(user);
        }
    }

    public <T extends DataSet> T load(long id, Class<T> clazz) {
        try (Session session = sessionFactory.openSession()) {
            UsersDAO dao = new UsersDAO(session);
            T object = dao.load(clazz, id);
            return object;
        }
    }

    @Override
    public <T extends DataSet> String getUserById(long id, Class<T> clazz) {
        try (Session session = sessionFactory.openSession()) {
            UsersDAO dao = new UsersDAO(session);
            String userById = dao.getUserById(id, clazz);
            return userById;
        }
    }


    @Override
    public Integer getCountUsers() {
        try (Session session = sessionFactory.openSession()) {
            UsersDAO dao = new UsersDAO(session);
            Integer countUsers = dao.getCountUsers();
            return countUsers;
        }
    }

    public void shutdown() {
        sessionFactory.close();
    }
}
