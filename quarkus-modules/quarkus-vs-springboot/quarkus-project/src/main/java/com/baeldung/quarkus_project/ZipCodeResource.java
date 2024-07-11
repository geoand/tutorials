package com.baeldung.quarkus_project;

import io.smallrye.mutiny.Uni;

import io.vertx.sqlclient.DatabaseException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import java.util.List;
import jakarta.ws.rs.core.MediaType;

@Path("/zipcode")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ZipCodeResource {

    private final ZipCodeRepo zipRepo;

    public ZipCodeResource(ZipCodeRepo zipRepo) {
            this.zipRepo = zipRepo;
        }

    @GET
    @Path("/{zipcode}")
    public Uni<ZipCode> findById(@PathParam("zipcode") String zipcode) {
        return zipRepo.findById(zipcode);
    }

    @GET
    @Path("/by_city")
    public Uni<List<ZipCode>> postZipCode(@QueryParam("city") String city) {
        return zipRepo.findByCity(city);
    }

    @POST
    public Uni<ZipCode> create(ZipCode zipCode) {
        return zipRepo.findById(zipCode.zip())
            .onItem()
            .ifNull()
            .switchTo(zipRepo.save(zipCode))
            .onFailure(this::isKeyDuplicated)
            .recoverWithUni(() -> zipRepo.findById(zipCode.zip()));
    }

    private boolean isKeyDuplicated(Throwable ex) {
        if (ex instanceof DatabaseException dbe) {
            return dbe.getErrorCode() == 1062;
        }
        return false;
    }

}
