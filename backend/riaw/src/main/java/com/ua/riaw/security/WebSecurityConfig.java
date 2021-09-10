package com.ua.riaw.security;

import com.ua.riaw.security.jwt.AuthEntryPointJwt;
import com.ua.riaw.security.jwt.AuthTokenFilter;
import com.ua.riaw.security.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }


    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        /* publicly available endpoints */
        String[] publicEndpoints = {
            // Swagger / OAS
            "/v3/api-docs/**",
            "/swagger-ui/**",
            // Sign up and login
            "/v1/api/users/signup",
            "/v1/api/users/signin"
        };

        http.cors().and().csrf().disable()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests().antMatchers(publicEndpoints).permitAll().and()
                .authorizeRequests().antMatchers("/v1/api/**").authenticated();

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        /*
        http.cors().and().csrf().disable()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests().antMatchers("/v1/api/users/**").permitAll().and()
                .authorizeRequests().antMatchers("/v1/api/etl/**").permitAll().and()
                .authorizeRequests().antMatchers("/v1/api/ehrTable/**").permitAll().and()
                .authorizeRequests().antMatchers("/v1/api/omopTable/**").permitAll().and()
                .authorizeRequests().antMatchers("/v1/api/tableMap/**").permitAll().and()
                .authorizeRequests().antMatchers("/v1/api/ehrField/**").permitAll().and()
                .authorizeRequests().antMatchers("/v1/api/omopField/**").permitAll().and()
                .authorizeRequests().antMatchers("v1/api/fieldMap/**").permitAll()
                .antMatchers("/api/test/**").permitAll()
                .anyRequest().authenticated();
         */
    }
}