package de.mpg.mpdl.inge.db.spring_config;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.SharedCacheMode;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaSessionFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import de.mpg.mpdl.inge.util.PropertyReader;

@Configuration
@ComponentScan("de.mpg.mpdl.inge.db.repository")
@EnableJpaRepositories(basePackages = "de.mpg.mpdl.inge.db.repository",
    entityManagerFactoryRef = "entityManagerFactory", transactionManagerRef = "transactionManager")
@EnableTransactionManagement
public class JPAConfiguration {



  @Bean
  @Primary
  public LocalContainerEntityManagerFactoryBean entityManagerFactory() throws Exception {
    LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
    em.setDataSource(restDataSource());
    em.setPackagesToScan(new String[] {"de.mpg.mpdl.inge.db.model"});
    JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
    em.setJpaVendorAdapter(vendorAdapter);
    em.setJpaProperties(hibernateProperties());
    em.setSharedCacheMode(SharedCacheMode.ENABLE_SELECTIVE);
    return em;
  }


  @Bean
  public HibernateJpaSessionFactoryBean sessionFactory(EntityManagerFactory emf) {

    HibernateJpaSessionFactoryBean sessionFactory = new HibernateJpaSessionFactoryBean();
    sessionFactory.setEntityManagerFactory(emf);

    return sessionFactory;
  }



  /*
   * @Bean public LocalSessionFactoryBean sessionFactory() { LocalSessionFactoryBean sessionFactory
   * = new LocalSessionFactoryBean(); sessionFactory.setDataSource(restDataSource());
   * sessionFactory.setPackagesToScan(new String[] {"de.mpg.mpdl.inge.model"});
   * sessionFactory.setHibernateProperties(hibernateProperties());
   * 
   * return sessionFactory; }
   */


  @Bean
  @Primary
  public DataSource restDataSource() throws Exception {
    ComboPooledDataSource dataSource = new ComboPooledDataSource();

    dataSource.setDriverClass(PropertyReader.getProperty("inge.database.driver.class"));
    dataSource.setJdbcUrl(PropertyReader.getProperty("inge.database.jdbc.url"));
    dataSource.setUser(PropertyReader.getProperty("inge.database.user.name"));
    dataSource.setPassword(PropertyReader.getProperty("inge.database.user.password"));
    dataSource.setMaxPoolSize(20);
    dataSource.setMinPoolSize(5);
    dataSource.setMaxStatements(50);
    dataSource.setInitialPoolSize(10);

    return dataSource;
  }



  @Bean
  @Primary
  public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
    JpaTransactionManager transactionManager = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory(emf);

    return transactionManager;
  }



  /*
   * 
   * @Bean
   * 
   * @Autowired public HibernateTransactionManager transactionManager(SessionFactory sessionFactory)
   * {
   * 
   * HibernateTransactionManager txManager = new HibernateTransactionManager();
   * txManager.setSessionFactory(sessionFactory);
   * 
   * return txManager; }
   */

  /*
   * 
   * @Bean public PersistenceExceptionTranslationPostProcessor exceptionTranslation() { return new
   * PersistenceExceptionTranslationPostProcessor(); }
   */
  Properties hibernateProperties() {
    return new Properties() {
      {
        setProperty("hibernate.hbm2ddl.auto", "update");
        setProperty("hibernate.dialect", "de.mpg.mpdl.inge.db.spring_config.JsonPostgreSQL9Dialect");
        setProperty("hibernate.cache.use_second_level_cache", "true");
        setProperty("hibernate.cache.use_query_cache", "true");
        setProperty("hibernate.cache.region.factory_class",
            "org.hibernate.cache.ehcache.EhCacheRegionFactory");
        setProperty("hibernate.jdbc.time_zone", "UTC");
        // setProperty("hibernate.generate_statistics", "true");

        // Makes it slow if set tot true
        setProperty("show_sql", "false");
      }
    };
  }



}
