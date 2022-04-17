package com.example.timesheet;

import com.example.timesheet.createNewUser.CreateNewUserController;
import com.example.timesheet.security.MyAuthenticationSuccesHandler;
import com.example.timesheet.security.WebSecurityConfig;
import org.apache.logging.log4j.core.config.Configurator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@SpringBootApplication(exclude = {HibernateJpaAutoConfiguration.class})
public class TimesheetApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext applicationContext = SpringApplication.run(TimesheetApplication.class, args);


//		String[] springBootAppBeanName = applicationContext.getBeanNamesForAnnotation(org.springframework.boot.autoconfigure.SpringBootApplication.class);
//		TimesheetApplication tmp = (TimesheetApplication) applicationContext.getBean(springBootAppBeanName[0]); // timesheetApplication


	}
}
