package org.example;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.NoResultException;

import java.util.Optional;

public class UserRepository {

    private static EntityManagerFactory emf;

    public UserRepository() {
        if (emf == null) {
            emf = Persistence.createEntityManagerFactory("restaurantPU");
        }
    }

    // Metoda pentru a crea un user nou (folosită de Admin sau la seed)
    public void adaugaUser(User user) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    // Metoda de Login: Caută userul după username și parolă
    public Optional<User> login(String username, String password) {
        EntityManager em = emf.createEntityManager();
        try {
            User user = em.createQuery(
                            "SELECT u FROM User u WHERE u.username = :user AND u.password = :pass", User.class)
                    .setParameter("user", username)
                    .setParameter("pass", password)
                    .getSingleResult();
            return Optional.of(user);
        } catch (NoResultException e) {
            return Optional.empty(); // Login eșuat
        } finally {
            em.close();
        }
    }

    // Metoda nouă pentru ștergerea unui user (Admin)
    public void stergeUser(String username) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            // Căutăm userul după nume ca să avem obiectul complet
            User user = em.createQuery("SELECT u FROM User u WHERE u.username = :nume", User.class)
                    .setParameter("nume", username)
                    .getSingleResult();

            em.remove(user); // Ștergem
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        } finally {
            em.close();
        }
    }

    // Metoda ajutătoare pentru a lua toți userii (pentru lista din Admin)
    public java.util.List<User> getAllUsers() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT u FROM User u", User.class).getResultList();
        } finally {
            em.close();
        }
    }
}