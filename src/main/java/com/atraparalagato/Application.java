package com.atraparalagato;

import com.atraparalagato.base.repository.H2GameRepository;
import com.atraparalagato.base.service.GameService;
import com.atraparalagato.base.strategy.AStarCatMovement;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:testdb");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        return dataSource;
    }

    @Bean
    public H2GameRepository h2GameRepository(DataSource dataSource) {
        return new H2GameRepository(dataSource);
    }

    @Bean
    public GameService gameService(H2GameRepository h2GameRepository) {
        return new GameService(h2GameRepository, new AStarCatMovement());
    }
}