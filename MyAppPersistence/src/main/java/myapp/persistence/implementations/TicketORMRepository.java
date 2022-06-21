package myapp.persistence.implementations;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import myapp.model.entities.Ticket;
import myapp.persistence.interfaces.ITicketRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TicketORMRepository implements ITicketRepository {
    private final SessionFactory sessionFactory;

    public TicketORMRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Ticket create(Ticket entity) {
        Ticket toReturn = null;
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                Integer id = (Integer) session.save(entity);
                tx.commit();
                entity.setId(id);
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
    public Ticket read(Integer id) {
        Ticket toReturn = null;
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = null;
            try {
                Ticket aux;
                transaction = session.beginTransaction();
                aux = session.get(Ticket.class, id);
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
    public Ticket update(Ticket entity) {
        Ticket toReturn = null;
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                Ticket aux;
                tx = session.beginTransaction();
                aux = session.get(Ticket.class, entity.getId());
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
    public Ticket delete(Integer id) {
        Ticket toReturn = null;
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                Ticket aux;
                tx = session.beginTransaction();
                aux = session.get(Ticket.class, id);
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
    public Collection<Ticket> readAll() {
        List<Ticket> toReturn = new ArrayList<>();
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                toReturn = session.createQuery("from Ticket as t", Ticket.class)
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
    public Iterable<Ticket> findAll() {
        return readAll();
    }
}
