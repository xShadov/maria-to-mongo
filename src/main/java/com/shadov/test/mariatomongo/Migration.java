package com.shadov.test.mariatomongo;

import com.shadov.test.mariatomongo.maria.LegacyCustomer;
import com.shadov.test.mariatomongo.maria.LegacyCustomerRepository;
import com.shadov.test.mariatomongo.mongo.ShinyCustomerRepository;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Component
@Order(2)
public class Migration implements ApplicationListener<ContextRefreshedEvent> {

	private static final Logger log = LoggerFactory.getLogger(Migration.class);

	@Value("${do.migration}")
	private boolean doMigration;

	@Value("${workers.count}")
	private int workerCount;

	@Autowired
	private LegacyCustomerRepository legacyCustomerRepository;

	@Autowired
	private ShinyCustomerRepository shinyCustomerRepository;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (!doMigration)
			return;

		log.info("Starting database migration");

		Try.run(this::migration)
				.onSuccess(noValue -> log.info("Database migration successful"))
				.onFailure(ex -> log.error("Database migration failed", ex));
	}

	private void migration() {
		final ExecutorService executor = Executors.newFixedThreadPool(workerCount);

		//dateBasedMigration(executor);
		idBasedMigration(executor);

		executor.shutdown();
		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException ex) {
			throw new RuntimeException(ex);
		}
	}

	private void idBasedMigration(ExecutorService executor) {
		final Integer maximumId = legacyCustomerRepository.findMaximumId();

		IntStream.range(0, workerCount)
				.forEach(number -> executor.submit(() -> migrateUpTo(maximumId, number)));
	}

	private void migrateUpTo(Integer maximumId, int worker) {
		Stream.iterate(1, i -> i + 10000)
				.limit((maximumId / 10000) + 5)
				.filter(num -> (num / 10000) % workerCount == worker)
				.peek(num -> log.info(String.format("Worker %d processing ids %d - %d"), worker, num, num + 10000))
				.map(num -> legacyCustomerRepository.findByIdGreaterThanEqualAndIdLessThan(num, num + 10000))
				.flatMap(Collection::stream)
				.map(LegacyCustomer::toShinyCustomer)
				.map(shinyCustomerRepository::insert)
				.forEach(Mono::block);
	}

	private void dateBasedMigration(ExecutorService executor) {
		final LocalDateTime minimumDate = legacyCustomerRepository.findMinimumDate();

		IntStream.range(0, workerCount)
				.forEach(number -> executor.submit(() -> migrateStartingAtDate(minimumDate.toLocalDate(), number)));
	}

	private void migrateStartingAtDate(LocalDate startingDate, int worker) {
		Stream.iterate(startingDate, date -> date.plusDays(1))
				.limit(ChronoUnit.DAYS.between(startingDate, LocalDateTime.now().plusDays(2)))
				.filter(date -> date.getDayOfMonth() % workerCount == worker)
				.peek(date -> log.info(String.format("Worker %d processing date %s", worker, date)))
				.map(date -> legacyCustomerRepository.findByBirthDateBetween(date.atStartOfDay(), date.atTime(LocalTime.MAX)))
				.flatMap(Collection::stream)
				.map(LegacyCustomer::toShinyCustomer)
				.map(shinyCustomerRepository::insert)
				.forEach(Mono::block);
	}
}