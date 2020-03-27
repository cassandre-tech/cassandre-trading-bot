package tech.cassandre.trading.strategy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Application start.
 */
@SuppressWarnings({ "checkstyle:FinalClass", "checkstyle:HideUtilityClassConstructor" })
@SpringBootApplication
public class Application {

	public static void main(final String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
