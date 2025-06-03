package tb.wca;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public class AbstractContainerBaseTest {

    private static final String DB_NAME = "testdb";
    private static final String DB_USER = "testuser";
    private static final String DB_PASSWORD = "testpass";

    static final GenericContainer<?> postgres = new GenericContainer<>(
            DockerImageName.parse("postgres:16-alpine"))
            .withEnv("POSTGRES_DB", DB_NAME)
            .withEnv("POSTGRES_USER", DB_USER)
            .withEnv("POSTGRES_PASSWORD", DB_PASSWORD)
            .withExposedPorts(5432);

    static {
        postgres.start();
    }

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        String jdbcUrl = String.format(
                "jdbc:postgresql://%s:%d/%s",
                postgres.getHost(),
                postgres.getMappedPort(5432),
                DB_NAME
        );

        registry.add("spring.datasource.url", () -> jdbcUrl);
        registry.add("spring.datasource.username", () -> DB_USER);
        registry.add("spring.datasource.password", () -> DB_PASSWORD);

        registry.add("spring.flyway.url", () -> jdbcUrl);
        registry.add("spring.flyway.user", () -> DB_USER);
        registry.add("spring.flyway.password", () -> DB_PASSWORD);
        registry.add("spring.flyway.locations", () -> "classpath:db/migration");
    }

}
