package com.example.debugagent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.scheduler.Schedulers;
import reactor.tools.agent.ReactorDebugAgent;

@SpringBootApplication
public class DebugAgentApplication {

	static boolean hooks = true;
	static boolean debugAgent = false;


	public static void main(String args[]) {
		if (hooks) {
			Hooks.onOperatorDebug();
		}
		if (debugAgent) {
			ReactorDebugAgent.init();
			ReactorDebugAgent.processExistingClasses();
		}
		SpringApplication.run(DebugAgentApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void go() {
		Flux
			.range(0, 5)
			.single()
			.subscribeOn(Schedulers.parallel())
			.block();
	}
}
