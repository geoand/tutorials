package com.baeldung.spring_project;

import io.r2dbc.spi.Readable;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class ZipRepo {

    private final DatabaseClient client;
    private final TransactionalOperator transactionalOperator;

    public ZipRepo(DatabaseClient client, TransactionalOperator transactionalOperator) {
        this.client = client;
        this.transactionalOperator = transactionalOperator;
    }


    public Flux<ZipCode> findByCity(String city) {
        return client.sql("SELECT zip, type, city, state, county, timezone FROM zipcode WHERE city = :city")
                .bind("city", city)
                .map(ZipRepo::fromRow)
                .all();
    }

    public Mono<ZipCode> save(ZipCode zipCode) {
        return transactionalOperator.transactional(client.sql("INSERT INTO zipcode (zip, type, city, state, county, timezone) VALUES (:zip, :type, :city, :state, :county, :timezone)")
                .bind("zip", zipCode.zip())
                .bind("type", zipCode.type())
                .bind("city", zipCode.city())
                .bind("state", zipCode.state())
                .bind("county", zipCode.county())
                .bind("timezone", zipCode.timezone())
                .map(r -> zipCode)
                .first())
                .switchIfEmpty(Mono.just(zipCode));

    }

    public Mono<ZipCode> findById(String id) {
        return client.sql("SELECT zip, type, city, state, county, timezone FROM zipcode WHERE zip = :zip")
                .bind("zip", id)
                .map(ZipRepo::fromRow)
                .first();
    }

    private static ZipCode fromRow(Readable readable) {
        return new ZipCode(
                readable.get("zip", String.class),
                readable.get("type", String.class),
                readable.get("city", String.class),
                readable.get("state", String.class),
                readable.get("county", String.class),
                readable.get("timezone", String.class)
        );
    }
}
