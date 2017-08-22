package com.vivek.audit.config;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import org.hibernate.jpa.boot.spi.EntityManagerFactoryBuilder;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration /*
				 * @ComponentScan(basePackages={"com.cycleon.audit.config",
				 * "com.cycleon.audit.service","com.cycleon.audit.jpa"})
				 */
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {
		"com.vivek.audit.jpa" }, entityManagerFactoryRef = "auditEntityManagerFactory", transactionManagerRef = "auditTransactionManager")
@PropertySource(value = { "file:${ANYPOINT_CONFIG}/audit.properties" })
public class AuditConfig {

	@Value("${audit.datasource.url}")
	private String url;
	@Value("${audit.datasource.username}")
	private String userName;
	@Value("${audit.datasource.password}")
	private String password;
	@Value("${audit.datasource.driver-class-name}")
	private String driver;

	@Bean(name = "auditDataSource")
	public DataSource auditDataSource() {
		DataSource datasource = createDataSource();
		System.out.println("Entering AUDITCONFIG CLASS");
		return datasource;
	}

	public DataSource createDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setUrl(url);
		dataSource.setUsername(userName);
		dataSource.setPassword(password);
		dataSource.setDriverClassName(driver);

		return dataSource;
	}

	@Bean(name = "auditEntityManagerFactory", autowire = Autowire.NO)
	public LocalContainerEntityManagerFactoryBean auditEntityManagerFactory() {
		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setJpaVendorAdapter(getVendorAdapter());
		factory.setDataSource(createDataSource());
		factory.setPackagesToScan("com.vivek.audit.model");
		return factory;
	}

	@Bean(name = "auditTransactionManager")
	public PlatformTransactionManager auditTransactionManager() {
		JpaTransactionManager txManager = new JpaTransactionManager();
		txManager.setEntityManagerFactory(auditEntityManagerFactory().getObject());
		return txManager;
	}

	public HibernateJpaVendorAdapter getVendorAdapter() {
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		vendorAdapter.setDatabase(Database.POSTGRESQL);
		vendorAdapter.setGenerateDdl(true);
		vendorAdapter.setShowSql(true);
		return vendorAdapter;
	}

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
		propertySourcesPlaceholderConfigurer.setIgnoreUnresolvablePlaceholders(true);
		return propertySourcesPlaceholderConfigurer;
	}
}
