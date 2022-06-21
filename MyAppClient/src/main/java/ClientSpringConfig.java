import myapp.network.rpcprotocol.RpcServerProxy;
import myapp.services.interfaces.ISuperService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Properties;

@Configuration
public class ClientSpringConfig {


    @Bean
    Properties properties() {
        Properties properties = new Properties();
        try {
            properties.load(ClientSpringConfig.class.getResourceAsStream("/client.properties"));
            ///
            System.out.println("Properties loaded:");
            properties.list(System.out);
            ///
        } catch (IOException e) {
            e.printStackTrace();
            properties = null;
        }
        return properties;
    }

    @Bean
    String host() {
        String host = null;
        try {
            host = properties().getProperty("server.host");

            ///
            System.out.println("Host loaded:" + host);
            ///
        } catch (NumberFormatException nef) {
            nef.printStackTrace();
        }
        return host;
    }

    @Bean
    Integer port() {
        Integer port = null;
        try {
            port = Integer.parseInt(properties().getProperty("server.port"));

            ///
            System.out.println("Host loaded:" + port);
            ///
        } catch (NumberFormatException nef) {
            nef.printStackTrace();
        }
        return port;
    }

    @Bean
    ISuperService superService() {
        return new RpcServerProxy(host(), port());
    }
}
