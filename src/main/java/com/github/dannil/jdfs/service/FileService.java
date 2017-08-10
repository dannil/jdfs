package com.github.dannil.jdfs.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import com.github.dannil.jdfs.manager.PersistenceManager;
import com.github.dannil.jdfs.model.File;

public class FileService {

    private EntityManager entityManager;

    public FileService() {
        this.entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
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

    public File update(File file) {
        EntityTransaction tx = this.entityManager.getTransaction();

        tx.begin();
        File f = this.entityManager.merge(file);
        tx.commit();

        return f;
    }

    public void saveOrUpdate(File file) {
        EntityTransaction tx = this.entityManager.getTransaction();

        tx.begin();
        // File f = getByPath(file.getPath());
        // if (f != null) {
        // this.entityManager.refresh(file);
        // } else {
        // this.entityManager.merge(file);
        // }
        //
        // this.entityManager.merge(file);
        tx.commit();
    }

    public void remove(File file) {
        EntityTransaction tx = this.entityManager.getTransaction();

        tx.begin();
        this.entityManager.remove(file);
        tx.commit();
    }

    public File getByPath(String path) {
        Query query = this.entityManager.createQuery("SELECT f FROM File f WHERE f.path = :path");
        query.setParameter("path", path);
        // List<File> fileWrapper = query.getResultList();
        return getSingleResult(query);
        // return this.entityManager.find(File.class, path);
    }

    public File getById(int id) {
        Query query = this.entityManager.createQuery("SELECT f FROM File f WHERE f.id = :id");
        query.setParameter("id", id);
        return getSingleResult(query);
    }

    public List<File> getAll() {
        Query query = this.entityManager.createQuery("SELECT f FROM File f");
        return query.getResultList();
    }

    private <T> T getSingleResult(Query query) {
        List<T> list = query.getResultList();
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

}
