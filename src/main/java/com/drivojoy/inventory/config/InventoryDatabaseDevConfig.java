package com.drivojoy.inventory.config;

import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.drivojoy.inventory.helpers.CustomAsyncExceptionHandler;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages="com.drivojoy.inventory.repositories")
@PropertySource("classpath:com/drivojoy/config/dev/db-config.properties")
@Profile("dev")
@EnableAsync
@EnableScheduling
@EnableAspectJAutoProxy
public class InventoryDatabaseDevConfig implements AsyncConfigurer, SchedulingConfigurer  {

	private static final Logger logger = LoggerFactory.getLogger(InventoryDatabaseDevConfig.class);
	private boolean initDatabase = false;
	private String environment = "dev";
	@Autowired 
	private Environment env;
	
	@Bean 
	public DataSource dataSource(){
		
		BasicDataSource dataSource = new BasicDataSource(); 
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://localhost:3306/drivojoy_inventory_prod"); 
		dataSource.setUsername("azureuser");
		dataSource.setPassword("LS1setup!"); 
		return dataSource; 
	}
	
	@Bean
	public DataSourceInitializer customDataSourceInitializer(DataSource dataSource) 
	{
		DataSourceInitializer customDataSourceInitializer = new DataSourceInitializer();
		customDataSourceInitializer.setDataSource(dataSource);
		ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
		logger.debug("Loading db.sql from : "+environment);
		databasePopulator.addScript(new ClassPathResource("com/drivojoy/config/"+environment+"/db.sql"));
		databasePopulator.addScript(new ClassPathResource("com/drivojoy/config/"+environment+"/procedures.sql"));
		databasePopulator.setSeparator("#");
		customDataSourceInitializer.setDatabasePopulator(databasePopulator);
		customDataSourceInitializer.setEnabled(initDatabase);
		return customDataSourceInitializer;
	}
	
	@Bean 
	public PlatformTransactionManager transactionManager() {
		EntityManagerFactory factory = entityManagerFactory(dataSource(), env).getObject(); 
		return new JpaTransactionManager(factory); 
	}
	
	@Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource, 
                                                                Environment env) {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);
        vendorAdapter.setShowSql(true);
        entityManagerFactoryBean.setDataSource(dataSource);
        entityManagerFactoryBean.setJpaVendorAdapter(vendorAdapter);
        entityManagerFactoryBean.setPackagesToScan("com.drivojoy.inventory.models", "org.springframework.data.jpa.convert.threeten");
 
        Properties jpaProperties = new Properties();
     
        //Configures the used database dialect. This allows Hibernate to create SQL
        //that is optimized for the used database.
        jpaProperties.put("hibernate.dialect", env.getRequiredProperty("hibernate.dialect"));
 
        //Specifies the action that is invoked to the database when the Hibernate
        //SessionFactory is created or closed.
        jpaProperties.put("hibernate.hbm2ddl.auto", 
        		env.getRequiredProperty("hibernate.hbm2ddl.auto")
        );
 
        //Configures the naming strategy that is used when Hibernate creates
        //new database objects and schema elements
        jpaProperties.put("hibernate.ejb.naming_strategy", 
        		env.getRequiredProperty("hibernate.ejb.naming_strategy")
        );
 
        //If the value of this property is true, Hibernate writes all SQL
        //statements to the console.
        jpaProperties.put("hibernate.show_sql", 
        		env.getRequiredProperty("hibernate.show_sql")
        );
 
        //If the value of this property is true, Hibernate will format the SQL
        //that is written to the console.
        jpaProperties.put("hibernate.format_sql", 
        		env.getRequiredProperty("hibernate.format_sql")
        );
 
        entityManagerFactoryBean.setJpaProperties(jpaProperties);
 
        return entityManagerFactoryBean;
    }
	
	@Bean
	JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }
	
	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
	    return new CustomAsyncExceptionHandler();
	}
	
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(20);
        executor.setMaxPoolSize(100);
        executor.setQueueCapacity(200);
        executor.initialize();
        return executor;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskExecutor());
    }
    
    @Bean(destroyMethod="shutdown")
    public Executor taskExecutor() {
        return Executors.newScheduledThreadPool(100);
    }

}
