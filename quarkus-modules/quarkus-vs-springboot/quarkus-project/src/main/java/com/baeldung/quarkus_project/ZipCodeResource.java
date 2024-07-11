package com.baeldung.quarkus_project;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import java.util.List;
import jakarta.persistence.PersistenceException;
import jakarta.ws.rs.core.MediaType;

@Path("/zipcode")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ZipCodeResource {

    private ZipCodeRepo zipRepo;

    public ZipCodeResource(ZipCodeRepo zipRepo) {
            this.zipRepo = zipRepo;
        }

    @GET
    @Path("/{zipcode}")
    public Uni<ZipCode> findById(@PathParam("zipcode") String zipcode) {
        return getById(zipcode);
    }

    @GET
    @Path("/by_city")
    public Uni<List<ZipCode>> postZipCode(@QueryParam("city") String city) {
        return zipRepo.findByCity(city);
    }

    @POST
    public Uni<ZipCode> create(ZipCode zipCode) {
        return getById(zipCode.getZip())
            .onItem()
            .ifNull()
            .switchTo(createZipCode(zipCode))
            .onFailure(PersistenceException.class)
            .recoverWithUni(() -> getById(zipCode.getZip()));
    }

    private Uni<ZipCode> getById(String zipCode) {
        return zipRepo.findById(zipCode);
    }

    private Uni<ZipCode> createZipCode(ZipCode zipCode) {
        return Uni.createFrom().deferred(() -> zipRepo.save(zipCode));
    }
}
