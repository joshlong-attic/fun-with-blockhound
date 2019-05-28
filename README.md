# Notes on How to Debug Reactor-based Applications

## Notes: 
* https://www.baeldung.com/spring-debugging-reactive-streams
* https://spring.io/blog/2019/03/06/flight-of-the-flux-1-assembly-vs-subscription
* https://spring.io/blog/2019/03/28/reactor-debugging-experience

## Ideas 

* Reactor Debug Agent: `compile 'io.projectreactor:reactor-tools:$LATEST_RELEASE`

* Use Blockhound by adding 

	```
	<dependency>
			<groupId>io.projectreactor.tools</groupId>
			<artifactId>blockhound</artifactId>
			<version>1.0.0.M3</version>
		</dependency>
	```

	and then in your code, [add code as in this example](https://github.com/joshlong/fun-with-blockhound/blob/master/src/main/java/com/example/blocked/BlockedApplication.java)

* onError callback in `#subscribe` or `#doOnError`:
 ```
  flux.subscribe(foo -> {
        logger.debug("Finished processing Foo with Id {}", foo.getId());
    }, error -> {
        logger.error(
          "The following error happened on processFoo method!",
           error);
    });
  ``` 

  or ```flux.doOnError(error -> {
    logger.error("The following error happened on processFoo method!", error);
}).subscribe();```

* `Hooks.onOperatorDebug();`: After the debug mode gets activated, our exception logs will include some helpful information. When u error, the first section in output is the thread stack trace (that is, ONLY the flow of execution on a given thread; not super useful for reactive programming), but the following sections provide information about (1) The assembly trace of the publisher — here we can confirm that the error was first generated in the processFoo method. (2) the operators that observed the error after it was first triggered, with the user class where they were chained.

* `    flux = flux.checkpoint("Observed error on processFoo", true);` : this will enable debug mode on just one pipeline 

* By calling the `#log()` method in our reactive chain, the application will log each element in the flow with the state that it has at that stage.

