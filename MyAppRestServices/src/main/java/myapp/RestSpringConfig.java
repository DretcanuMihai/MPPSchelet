package myapp;

import myapp.model.validators.interfaces.IFestivalValidator;
import myapp.persistence.implementations.FestivalORMRepository;
import myapp.persistence.interfaces.IFestivalRepository;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import myapp.model.validators.implementations.FestivalValidator;

@Configuration
public class RestSpringConfig {
    @Bean
    SessionFactory sessionFactory() {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure()
                .build();
        try {
            return new MetadataSources( registry ).buildMetadata().buildSessionFactory();
        }
        catch (Exception e) {
            System.err.println("Error creating session factory;");
            e.printStackTrace();
            StandardServiceRegistryBuilder.destroy( registry );
        }
        return null;
    }

    @Bean
    IFestivalRepository festivalRepository(){
       return new FestivalORMRepository(sessionFactory());

    }

    @Bean
    IFestivalValidator festivalValidator(){
       return new FestivalValidator();
    }
}
