package mate.academy.dao.impl;

import java.util.List;
import java.util.Optional;
import javax.persistence.criteria.CriteriaQuery;
import mate.academy.dao.UserDao;
import mate.academy.exception.DataProcessingException;
import mate.academy.lib.Dao;
import mate.academy.model.User;
import mate.academy.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

@Dao
public class UserDaoImpl implements UserDao {
    @Override
    public Optional<User> findByEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("SELECT u FROM User u WHERE u.email =: email",
                    User.class).setParameter("email", email).uniqueResultOptional();
        } catch (Exception e) {
            throw new DataProcessingException("Can't find user by email " + email, e);
        }
    }

    @Override
    public List<User> allUsers() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaQuery<User> criteriaQuery =
                    session.getCriteriaBuilder().createQuery(User.class);
            criteriaQuery.from(User.class);
            return session.createQuery(criteriaQuery).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can not get all users", e);
        }
    }

    @Override
    public User add(User user) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            session.save(user);
            transaction.commit();
            return user;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't insert user " + user, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}