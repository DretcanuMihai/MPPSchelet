package myapp.persistence.implementations;

import myapp.persistence.interfaces.IFestivalRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import myapp.model.entities.Festival;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FestivalORMRepository implements IFestivalRepository {

    private final SessionFactory sessionFactory;
    public FestivalORMRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    @Override
    public Festival create(Festival entity) {
        Festival toReturn = null;
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
    public Festival read(Integer id) {
        Festival toReturn = null;
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = null;
            try {
                Festival aux;
                transaction = session.beginTransaction();
                aux = session.get(Festival.class, id);
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
    public Festival update(Festival entity) {
        Festival toReturn = null;
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                Festival aux;
                tx = session.beginTransaction();
                aux = session.get(Festival.class, entity.getId());
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
    public Festival delete(Integer id) {
        Festival toReturn = null;
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                Festival aux;
                tx = session.beginTransaction();
                aux = session.get(Festival.class, id);
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
    public Collection<Festival> readAll() {
        List<Festival> toReturn = new ArrayList<>();
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                toReturn = session.createQuery("from Festival as f", Festival.class)
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
    public Iterable<Festival> findAll() {
        return readAll();
    }

    @Override
    public Collection<Festival> readAllOnDate(LocalDate date) {
        List<Festival> toReturn = new ArrayList<>();
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                toReturn = session.createQuery("from Festival as f where DATE(f.dateTimeSql)=:date", Festival.class)
                        .setParameter("date", date.toString())
                        .list();
                transaction.commit();
            } catch (RuntimeException ex) {
                System.err.println("Error in read All On Date;");
                ex.printStackTrace();
                if (transaction != null)
                    transaction.rollback();
            }
        }
        return toReturn;
    }
}
