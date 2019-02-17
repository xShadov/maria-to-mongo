package com.shadov.test.mariatomongo.mongo;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ShinyCustomerRepository extends ReactiveMongoRepository<ShinyCustomer, String> {
}
