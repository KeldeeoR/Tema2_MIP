package org.example;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List;

public class ProdusRepository {

    private static EntityManagerFactory emf;

    public ProdusRepository() {
        if (emf == null) {
            // "restaurantPU" trebuie să fie exact numele din persistence.xml
            emf = Persistence.createEntityManagerFactory("restaurantPU");
        }
    }

    public void adaugaProdus(Produs produs) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(produs);
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

    public List<Produs> incarcaToateProdusele() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT p FROM Produs p", Produs.class).getResultList();
        } finally {
            em.close();
        }
    }

    // Metoda pentru ștergerea unui produs (Admin)
    public void stergeProdus(Produs produs) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            // Trebuie să dăm "merge" întâi pentru a atașa obiectul la sesiunea curentă, apoi remove
            Produs deSters = em.merge(produs);
            em.remove(deSters);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    // Metoda nouă pentru EDITARE (Admin)
    public void actualizeazaProdus(Produs produs) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(produs); // Merge face update dacă ID-ul există
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

}