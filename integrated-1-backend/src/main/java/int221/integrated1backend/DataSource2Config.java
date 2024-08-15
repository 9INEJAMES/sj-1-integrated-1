package int221.integrated1backend;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class DataSource2Config {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.second")
    public DataSourceProperties secondDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "secondDataSource")
    public DataSource secondDataSource() {
        DataSourceProperties properties = secondDataSourceProperties();
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(properties.getUrl());
        dataSource.setUsername(properties.getUsername());
        dataSource.setPassword(properties.getPassword());
        dataSource.setDriverClassName(properties.getDriverClassName());
        return dataSource;
    }
}
