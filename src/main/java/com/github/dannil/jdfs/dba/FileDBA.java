package com.github.dannil.jdfs.dba;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import com.github.dannil.jdfs.model.File;

public class FileDBA {

    private EntityManager entityManager;

    public FileDBA() {
        this.entityManager = DatabaseConnection.getSessionFactory().createEntityManager();
    }

    public void save(File file) {
        // Session session = sessionFactory.openSession();
        // Transaction tx = session.beginTransaction();

        EntityTransaction tx = this.entityManager.getTransaction();

        tx.begin();
        this.entityManager.persist(file);
        tx.commit();

        // session.save(file);

        // tx.commit();
        // session.close();
    }

    public void saveOrUpdate(File file) {
        EntityTransaction tx = this.entityManager.getTransaction();

        tx.begin();
        this.entityManager.merge(file);
        tx.commit();
    }

    public File getByPath(String path) {
        return this.entityManager.find(File.class, path);
    }

    public File getById(int id) {
        Query query = this.entityManager.createQuery("SELECT f FROM File f WHERE f.id = :id");
        query.setParameter("id", id);
        return (File) query.getSingleResult();
    }

    public List<File> getAll() {
        Query query = this.entityManager.createQuery("SELECT f FROM File f");
        return query.getResultList();
    }

}
