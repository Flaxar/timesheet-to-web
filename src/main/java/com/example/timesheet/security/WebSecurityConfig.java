package com.example.timesheet.security;

import com.example.timesheet.Credentials;
import com.example.timesheet.DatabaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.sql.DataSource;
import java.security.SecureRandom;
import java.util.*;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private DatabaseController databaseController;

    @Autowired
    private DataSource dataSource;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable();
        http.authorizeRequests().antMatchers("/employee", "/api/**").hasAnyRole("EMPLOYEE", "ADMIN")
                .and().authorizeRequests().antMatchers("/admin", "/create", "/admin/**").hasRole("ADMIN")
                .and().formLogin().loginPage("/login").permitAll().loginProcessingUrl("/login").successHandler(myAuthenticationSuccessHandler());
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .rolePrefix("ROLE_")
                .usersByUsernameQuery("SELECT username, password, enabled FROM credentials WHERE username = ?")
                .authoritiesByUsernameQuery("SELECT username, role AS authority FROM credentials WHERE username = ?")
                .passwordEncoder(getPasswordEncoder());
        auth.eraseCredentials(false);

    }


    public static UserDetails generateUserDetails(String username, String password, String roles) {
        return User
                .withUsername(username)
                .password(password)
                .roles(roles)
                .build();
    }

    @Bean
    protected PasswordEncoder getPasswordEncoder() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(10);

//        uncomment this if you want to manually encode a password
//        String encrypted = bCryptPasswordEncoder.encode("abcd1234");

        return bCryptPasswordEncoder;
    }

    @Bean
    protected AuthenticationSuccessHandler myAuthenticationSuccessHandler() {
        return new MyAuthenticationSuccesHandler();
    }

}
