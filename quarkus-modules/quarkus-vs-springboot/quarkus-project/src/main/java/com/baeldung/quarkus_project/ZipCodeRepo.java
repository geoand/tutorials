package com.baeldung.quarkus_project;

import io.smallrye.mutiny.Uni;
import java.util.List;
import jakarta.inject.Singleton;
import org.hibernate.reactive.mutiny.Mutiny;

@Singleton
public class ZipCodeRepo {

    private final Mutiny.SessionFactory sessionFactory;

    public ZipCodeRepo(Mutiny.SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Uni<List<ZipCode>> findByCity(String city) {
        return sessionFactory.withStatelessSession(s -> s.createNamedQuery("findByCity", ZipCode.class).setParameter("city", city).getResultList());
    }

    public Uni<ZipCode> save(ZipCode zipCode) {
        return sessionFactory.withTransaction(session -> session.persist(zipCode).replaceWith(zipCode));
    }

    public Uni<ZipCode> findById(String id) {
        return sessionFactory.withStatelessSession(s -> s.get(ZipCode.class, id));
    }
}
