package com.baeldung.quarkus_project;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;
import jakarta.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class ZipCodeRepo {

    private final MySQLPool client;

    public ZipCodeRepo(MySQLPool client) {
        this.client = client;
    }

    public Uni<List<ZipCode>> findByCity(String city) {
        return client.preparedQuery("SELECT zip, type, city, state, county, timezone FROM zipcode WHERE city = ?")
                .execute(Tuple.of(city))
                .map(rowSet -> {
                    final List<ZipCode> list = new ArrayList<>(rowSet.size());
                    for (final Row r : rowSet) {
                        list.add(fromRow(r));
                    }
                    return list;
                });
    }

    public Uni<ZipCode> save(ZipCode zipCode) {
        return client.withTransaction(conn -> {
            return conn.preparedQuery("INSERT INTO zipcode (zip, type, city, state, county, timezone) VALUES (?, ?, ?, ?, ?, ?) ")
                    .execute(Tuple.of(zipCode.zip(), zipCode.type(), zipCode.city(), zipCode.state(), zipCode.county(), zipCode.timezone()))
                    .onItem().transform(rows -> zipCode);
        });
    }

    public Uni<ZipCode> findById(String id) {
        return client.preparedQuery("SELECT zip, type, city, state, county, timezone FROM zipcode WHERE zip = ?")
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
