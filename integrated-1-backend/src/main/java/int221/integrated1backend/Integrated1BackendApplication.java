package int221.integrated1backend;

import int221.integrated1backend.configs.FileStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties({FileStorageProperties.class})
@SpringBootApplication
public class Integrated1BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(Integrated1BackendApplication.class, args);
    }

}
