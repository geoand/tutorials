package com.baeldung.quarkus_project;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.SqlConnection;
import io.vertx.mutiny.sqlclient.Tuple;
import jakarta.inject.Singleton;
import java.util.List;
import java.util.function.Function;

@Singleton
public class ZipCodeRepo {

    private final MySQLPool client;

    public ZipCodeRepo(MySQLPool client) {
        this.client = client;
    }

    public Uni<List<ZipCode>> findByCity(String city) {
        return client.preparedQuery("SELECT zip, type, city, state, county, timezone FROM ZipCode WHERE city = $1")
                .execute(Tuple.of(city))
                .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
                .onItem().transform(ZipCodeRepo::fromRow)
                .collect().asList();
    }

    public Uni<ZipCode> save(ZipCode zipCode) {
        if (zipCode.getZip() == null) {
            return client.withTransaction(conn -> {
                return conn.preparedQuery("""
INSERT INTO ZipCode (type, city, state, county, timezone) 
VALUES ($1, $2, $3, $4, $5) 
RETURNING zip
""")
                        .execute(Tuple.of(zipCode.getType(), zipCode.getCity(), zipCode.getState(), zipCode.getCounty(), zipCode.getTimezone()))
                        .onItem().transform(rows -> zipCode.withZip(rows.iterator().next().getString("zip")));
            });
        } else {
            return client.withTransaction(conn -> {
                return conn.preparedQuery("""
UPDATE ZipCode 
SET type = $1, city = $2, state = $3, county = $4, timezone = $5 
VALUES ($1, $2, $3, $4, $5) 
WHERE zip = %6
""")
                        .execute(Tuple.of(zipCode.getType(), zipCode.getCity(), zipCode.getState(), zipCode.getCounty(), zipCode.getTimezone(),zipCode.getCity()))
                        .onItem().transform(r -> zipCode);
            });
        }

    }

    public Uni<ZipCode> findById(String id) {
        return client.preparedQuery("SELECT zip, type, city, state, county, timezone FROM ZipCode WHERE zip = $1")
                .execute(Tuple.of(id))
                .onItem().transform(RowSet::iterator)
                .onItem().transform(iterator -> iterator.hasNext() ? fromRow(iterator.next()) : null);
    }

    private static ZipCode fromRow(Row row) {
        return new ZipCode(
                row.getString("zip"),
                row.getString("type"),
                row.getString("city"),
                row.getString("state"),
                row.getString("county"),
                row.getString("timezone"));
    }
}
