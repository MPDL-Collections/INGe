package de.mpg.mpdl.inge.service.spring;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import de.mpg.mpdl.inge.db.spring_config.JPAConfiguration;
import de.mpg.mpdl.inge.es.spring.AppConfig;
import de.mpg.mpdl.inge.filestorage.spring.AppConfigFileStorage;

@Configuration
@ComponentScan("de.mpg.mpdl.inge.service")
@Import({AppConfig.class, JPAConfiguration.class, AppConfigFileStorage.class})
@EnableTransactionManagement
@EnableScheduling
@PropertySource("classpath:pubman.properties")
public class AppConfigPubmanLogic {
  private final static Logger logger = LogManager.getLogger(AppConfigPubmanLogic.class);

  @Bean
  public PasswordEncoder passwordEncoder() {
    logger.info("Initializing Spring Bean PasswordEncoder");
    return new BCryptPasswordEncoder();
  }

  @Bean
  public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
    return new PropertySourcesPlaceholderConfigurer();
  }
}
