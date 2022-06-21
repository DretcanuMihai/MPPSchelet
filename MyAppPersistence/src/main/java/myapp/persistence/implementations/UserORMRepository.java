package myapp.persistence.implementations;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import myapp.model.entities.User;
import myapp.persistence.interfaces.IUserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserORMRepository implements IUserRepository {
    private final SessionFactory sessionFactory;

    public UserORMRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public User create(User entity) {
        User toReturn = null;
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                session.save(entity);
                tx.commit();
                toReturn = entity;
            } catch (RuntimeException ex) {
                System.err.println("Error in create;");
                ex.printStackTrace();
                if (tx != null)
                    tx.rollback();
            }
        }
        return toReturn;
    }

    @Override
    public User read(String id) {
        User toReturn = null;
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = null;
            try {
                User aux;
                transaction = session.beginTransaction();
                aux = session.get(User.class, id);
                transaction.commit();
                toReturn = aux;
            } catch (RuntimeException ex) {
                System.err.println("Error in read;");
                ex.printStackTrace();
                if (transaction != null)
                    transaction.rollback();
            }
        }
        return toReturn;
    }

    @Override
    public User update(User entity) {
        User toReturn = null;
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                User aux;
                tx = session.beginTransaction();
                aux = session.get(User.class, entity.getId());
                session.merge(entity);
                tx.commit();
                toReturn = aux;

            } catch (RuntimeException ex) {
                System.err.println("Error in update;");
                ex.printStackTrace();
                if (tx != null)
                    tx.rollback();
            }
        }
        return toReturn;
    }
    @Override
    public User delete(String id) {
        User toReturn = null;
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                User aux;
                tx = session.beginTransaction();
                aux = session.get(User.class, id);
                session.delete(aux);
                tx.commit();
                toReturn = aux;
            } catch (RuntimeException ex) {
                System.err.println("Error in delete;");
                ex.printStackTrace();
                if (tx != null)
                    tx.rollback();
            }
        }
        return toReturn;
    }

    @Override
    public Collection<User> readAll() {
        List<User> toReturn = new ArrayList<>();
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                toReturn = session.createQuery("from User as u", User.class)
                        .list();
                transaction.commit();
            } catch (RuntimeException ex) {
                System.err.println("Error in read All;");
                ex.printStackTrace();
                if (transaction != null)
                    transaction.rollback();
            }
        }
        return toReturn;
    }

    @Override
    public Iterable<User> findAll() {
        return readAll();
    }
}
