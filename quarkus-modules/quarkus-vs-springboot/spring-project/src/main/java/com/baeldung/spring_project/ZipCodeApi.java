package com.baeldung.spring_project;

import java.util.function.Function;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/zipcode")
public class ZipCodeApi {

    private final ZipRepo zipRepo;

    public ZipCodeApi(ZipRepo zipRepo) {
        this.zipRepo = zipRepo;
    }

    @GetMapping("/{zipcode}")
    public Mono<ZipCode> findById(@PathVariable String zipcode) {
        return getById(zipcode);
    }

    @GetMapping("/by_city")
    public Flux<ZipCode> postZipCode(@RequestParam String city) {
        return zipRepo.findByCity(city);
    }

    @PostMapping
    public Mono<ZipCode> create(@RequestBody ZipCode zipCode) {
        return getById(zipCode.zip())
                .switchIfEmpty(zipRepo.save(zipCode))
                .onErrorResume(this::isKeyDuplicated, this.recoverWith(zipCode));
    }

    private Mono<ZipCode> getById(String zipCode) {
        return zipRepo.findById(zipCode);
    }

    private boolean isKeyDuplicated(Throwable ex) {
        return ex instanceof DuplicateKeyException;
    }

    private Function<? super Throwable, ? extends Mono<ZipCode>> recoverWith(ZipCode zipCode) {
        return throwable -> zipRepo.findById(zipCode.zip());
    }
}
