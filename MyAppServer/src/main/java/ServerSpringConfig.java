import myapp.model.validators.implementations.FestivalValidator;
import myapp.model.validators.implementations.TicketValidator;
import myapp.model.validators.implementations.UserValidator;
import myapp.model.validators.interfaces.IFestivalValidator;
import myapp.model.validators.interfaces.ITicketValidator;
import myapp.model.validators.interfaces.IUserValidator;
import myapp.network.utils.AbstractServer;
import myapp.network.utils.AppConcurrentServer;
import myapp.persistence.implementations.FestivalORMRepository;
import myapp.persistence.implementations.TicketORMRepository;
import myapp.persistence.implementations.UserORMRepository;
import myapp.persistence.interfaces.IFestivalRepository;
import myapp.persistence.interfaces.ITicketRepository;
import myapp.persistence.interfaces.IUserRepository;
import myapp.server.FestivalService;
import myapp.server.SuperService;
import myapp.server.UserService;
import myapp.services.interfaces.IFestivalService;
import myapp.services.interfaces.ISuperService;
import myapp.services.interfaces.IUserService;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Properties;

@Configuration
public class ServerSpringConfig {
    @Bean
    SessionFactory sessionFactory() {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure()
                .build();
        try {
            return new MetadataSources(registry).buildMetadata().buildSessionFactory();
        } catch (Exception e) {
            System.err.println("Error creating session factory");
            e.printStackTrace();
            StandardServiceRegistryBuilder.destroy(registry);
        }
        return null;
    }

    @Bean
    IFestivalRepository festivalRepository() {
        return new FestivalORMRepository(sessionFactory());

    }

    @Bean
    ITicketRepository ticketRepository() {
        return new TicketORMRepository(sessionFactory());

    }

    @Bean
    IUserRepository userRepository() {
        return new UserORMRepository(sessionFactory());

    }

    @Bean
    IFestivalValidator festivalValidator() {
        return new FestivalValidator();
    }

    @Bean
    ITicketValidator ticketValidator() {
        return new TicketValidator();
    }

    @Bean
    IUserValidator userValidator() {
        return new UserValidator();
    }

    @Bean
    IFestivalService festivalService() {
        return new FestivalService(festivalRepository(), festivalValidator(), ticketRepository(), ticketValidator());
    }

    @Bean
    IUserService userService() {
        return new UserService(userValidator(), userRepository());
    }

    @Bean
    ISuperService superService() {
        return new SuperService(festivalService(), userService());
    }

    @Bean
    Properties properties() {
        Properties properties = new Properties();
        try {
            properties.load(ServerSpringConfig.class.getResourceAsStream("/server.properties"));
        } catch (IOException e) {
            System.err.println("Error loading properties;");
            e.printStackTrace();
            properties=null;
        }
        return properties;
    }

    @Bean
    Integer port() {
        Integer port = null;
        try {
            port = Integer.parseInt(properties().getProperty("server.port"));
        } catch (NumberFormatException nef) {
            System.err.println("Error loading port;");
            nef.printStackTrace();
        }
        return port;
    }

    @Bean
    AbstractServer server() {
        return new AppConcurrentServer(port(), superService());
    }
}
