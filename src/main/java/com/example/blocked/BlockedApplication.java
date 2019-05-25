package com.example.blocked;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import reactor.blockhound.BlockHound;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@SpringBootApplication
@Log4j2
public class BlockedApplication {

	public static void main(String[] args) {
		/*
		Blockhound detects blocked threads on threads created by Scheduler.* factory methods that implement `NonBlocking`.
		You can customize how it detects or what it detects by using BlockHoundIntegration instances when doing Blockhound.install(..).

		Blockhound installs the default Reactor configuration in ReactorBlockHoundIntegration,
		which is loaded using the Java 6 service loader API: /Users/joshlong/.m2/repository/io/projectreactor/reactor-core/3.3.0.M1/reactor-core-3.3.0.M1.jar!/META-INF/services/reactor.blockhound.integration.BlockHoundIntegration

		 */
		BlockHound.install();
		SpringApplication.run(BlockedApplication.class, args);
	}

	@SneakyThrows
	void block() {
		Thread.sleep(10000);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void go() {

		Scheduler canBlock = Schedulers.elastic(); // not going to work with Blockhound

		Scheduler canNotBlock = Schedulers.newParallel("p10", 10); // this will trigger Blockhound

		Flux.just("A", "B", "C", "D")
			.map(n -> n + n)
			.doOnNext(x -> block())
			.subscribeOn(canBlock)
			.subscribe();


	}


}
