package cc.viridian.service.statement.config;

import cc.viridian.provider.SenderConfig;
import cc.viridian.provider.StatementSenderProvider;
import cc.viridian.provider.spi.StatementSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

@Slf4j
@Service
@Configuration
public class SenderAdapterConfig {

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Value("${spring.cloud.config.uri}")
    private String springCloudConfigUrl;

    private HashMap<String, SenderConfig> loadedClasses;

    public HashMap<String, SenderConfig> getLoadedClasses() {
        return loadedClasses;
    }

    /**
     * Initialize sender adapters.
     *
     * @return list of valid adapters
     */
    @Bean
    public HashMap<String, SenderConfig> initializeAdapters() {
        try {
            StatementSenderProvider formatterProvider = StatementSenderProvider.getInstance();
            loadedClasses = formatterProvider.getAdapters();
            if (loadedClasses.size() == 0) {
                log.error("Fatal Error. There are zero Formatter adapters loaded in the system.");
                log.error("Check load class path to include valid Formatter adapters.");
                System.exit(1);
            }

            for (SenderConfig config : loadedClasses.values()) {
                config.loadConfigProperties(activeProfile, springCloudConfigUrl);
            }

        } catch (Exception e) {
            log.error("fatal error reading config properties from config server");
            log.error(e.getMessage());
            System.exit(1);
        }
        return loadedClasses;
    }

    /**
     * get Sender Adapter.
     *
     * @param senderId
     * @return
     */
    public StatementSender getSenderAdapter(final String senderId) {
        if (loadedClasses.containsKey(senderId)) {
            return loadedClasses.get(senderId).getAdapter();
        } else {
            return null;
        }
    }

}
