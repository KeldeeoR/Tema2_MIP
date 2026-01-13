package org.example;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List;

public class ComandaRepository {

    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("restaurantPU");

    public void salveazaMasa(Masa masa) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(masa);
        em.getTransaction().commit();
        em.close();
    }

    public List<Masa> getToateMesele() {
        EntityManager em = emf.createEntityManager();
        List<Masa> mese = em.createQuery("SELECT m FROM Masa m ORDER BY m.numarMasa", Masa.class).getResultList();
        em.close();
        return mese;
    }

    public void actualizeazaMasa(Masa masa) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.merge(masa); // Update
        em.getTransaction().commit();
        em.close();
    }

    public void salveazaComanda(Comanda comanda) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(comanda);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public List<Comanda> getComenziByUser(User user) {
        EntityManager em = emf.createEntityManager();
        List<Comanda> list = em.createQuery("SELECT c FROM Comanda c WHERE c.ospatar.id = :uid", Comanda.class)
                .setParameter("uid", user.getId())
                .getResultList();
        em.close();
        return list;
    }

    public Comanda gasesteComandaActiva(Masa masa) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT c FROM Comanda c WHERE c.masa.id = :masaId AND c.finalizata = false", Comanda.class)
                    .setParameter("masaId", masa.getId())
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        } finally {
            em.close();
        }
    }

    public List<Comanda> getToateComenzile() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT c FROM Comanda c JOIN FETCH c.ospatar ORDER BY c.dataOra DESC", Comanda.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

}