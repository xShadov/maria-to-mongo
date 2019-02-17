package com.shadov.test.mariatomongo.maria;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface LegacyCustomerRepository extends JpaRepository<LegacyCustomer, Integer> {
	@Query("select max(lc.id) from LegacyCustomer lc")
	Integer findMaximumId();

	@Query("select min(lc.birthDate) from LegacyCustomer lc")
	LocalDateTime findMinimumDate();

	@EntityGraph(value = "FULL_GRAPH", type = EntityGraph.EntityGraphType.LOAD)
	List<LegacyCustomer> findByBirthDateBetween(LocalDateTime after, LocalDateTime before);

	@EntityGraph(value = "FULL_GRAPH", type = EntityGraph.EntityGraphType.LOAD)
	List<LegacyCustomer> findByIdGreaterThanEqualAndIdLessThan(Integer after, Integer before);
}
