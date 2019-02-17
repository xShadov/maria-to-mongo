package com.shadov.test.mariatomongo;

import com.shadov.test.mariatomongo.maria.LegacyCustomer;
import com.shadov.test.mariatomongo.maria.LegacyCustomerRepository;
import io.vavr.control.Try;
import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.SplittableRandom;
import java.util.stream.IntStream;

@Component
@Order(1)
public class InitMaria implements ApplicationListener<ContextRefreshedEvent> {

	private static final Logger log = LoggerFactory.getLogger(InitMaria.class);

	@Autowired
	private LegacyCustomerRepository customerRepository;

	@Value("${init.maria}")
	private boolean initMaria;

	@Value("${init.maria.records}")
	private int initMariaRecords;

	private static final SplittableRandom random = new SplittableRandom();
	private static final RandomStringGenerator generator = new RandomStringGenerator.Builder()
			.withinRange('0', 'z')
			.filteredBy(CharacterPredicates.LETTERS)
			.build();

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (!initMaria)
			return;

		log.info("Staring initializing Maria Database");

		Try.run(this::initializeMaria)
				.onSuccess(noValue -> log.info("Finished initializing Maria Database"))
				.onFailure(ex -> log.error("Initializing Maria Database failed", ex));
	}

	private void initializeMaria() {
		IntStream.range(0, initMariaRecords)
				.forEach(number -> customerRepository.save(randomCustomer()));
	}

	private LegacyCustomer randomCustomer() {
		final LegacyCustomer customer = new LegacyCustomer();
		customer.setAge(random.nextInt(5, 40));
		customer.setCity(generator.generate(6));
		customer.setName(generator.generate(random.nextInt(3, 10)));
		customer.setBirthDate(randomLocalDateTime());
		return customer;
	}

	private LocalDateTime randomLocalDateTime() {
		return LocalDateTime.of(
				LocalDate.of(
						random.nextInt(2016, 2019),
						random.nextInt(1, 13),
						random.nextInt(1, 28)
				), LocalTime.of(
						random.nextInt(0, 24),
						random.nextInt(0, 60)
				)
		);
	}
}