package int221.integrated1backend;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Primary
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.first")
    public DataSourceProperties firstDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean(name = "firstDataSource")
    public DataSource firstDataSource() {
        DataSourceProperties properties = firstDataSourceProperties();
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(properties.getUrl());
        dataSource.setUsername(properties.getUsername());
        dataSource.setPassword(properties.getPassword());
        dataSource.setDriverClassName(properties.getDriverClassName());
        return dataSource;
    }
}
