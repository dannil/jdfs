package com.github.dannil.jdfs.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import com.github.dannil.jdfs.manager.PersistenceManager;
import com.github.dannil.jdfs.model.FileModel;

public class FileService {

    private EntityManager entityManager;

    public FileService() {
        this.entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
    }

    public void save(FileModel file) {
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

    public FileModel update(FileModel file) {
        EntityTransaction tx = this.entityManager.getTransaction();

        tx.begin();
        FileModel f = this.entityManager.merge(file);
        tx.commit();

        return f;
        // return saveOrUpdate(file);
    }

    // public File saveOrUpdate(File file) {
    // EntityTransaction tx = this.entityManager.getTransaction();
    //
    // tx.begin();
    //
    // // try {
    // // this.entityManager.persist(file);
    // // } catch (ConstraintViolationException e) {
    // //
    // // }
    //
    // // File f = getByPath(file.getPath());
    // // if (f == null) {
    // // this.entityManager.persist(file);
    // // } else {
    // // f.set
    // // }
    //
    // // if (f == null) {
    // //
    // // } else {
    // // this.entityManager.merge(file);
    // // }
    // tx.commit();
    // return file;
    // }

    public void remove(FileModel file) {
        EntityTransaction tx = this.entityManager.getTransaction();

        tx.begin();
        this.entityManager.remove(file);
        tx.commit();
    }

    public FileModel getByPath(String path) {
        Query query = this.entityManager.createQuery("SELECT f FROM FileModel f WHERE f.path = :path");
        query.setParameter("path", path);
        return getSingleResult(query);
    }

    public FileModel getById(int id) {
        Query query = this.entityManager.createQuery("SELECT f FROM FileModel f WHERE f.id = :id");
        query.setParameter("id", id);
        return getSingleResult(query);
    }

    public List<FileModel> getAll() {
        Query query = this.entityManager.createQuery("SELECT f FROM FileModel f");
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
