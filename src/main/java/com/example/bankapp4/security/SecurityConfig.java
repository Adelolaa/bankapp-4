package com.example.bankapp4.security;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
@AllArgsConstructor
    public class SecurityConfig {


        @Autowired
        private UserDetailsService userDetailsService;



        @Bean
        public static PasswordEncoder passwordEncoder(){
            return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
            return configuration.getAuthenticationManager();
        }

        @Bean
        public AuthenticationProvider authenticationProvider(){
            DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
            authenticationProvider.setUserDetailsService(userDetailsService);
            authenticationProvider.setPasswordEncoder(passwordEncoder());
            return authenticationProvider;
        }
// .requestMatchers("api/users/balanceEnquiry").permitAll()
//                                    .requestMatchers("/api/users/signin").permitAll()
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
            httpSecurity.csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(authorize ->
                            //authorize.anyRequest().authenticated())
                            authorize.requestMatchers("api/users/create","/swagger-resources/**","/swagger-ui/**","/v2/api-docs/**").permitAll()
                                    .requestMatchers("/api/users/nameEnquiry").hasRole("USER")
                                    .requestMatchers("api//users/credit").hasRole("USER")
                                    .requestMatchers("/api/users/all/api/debit").hasRole("USER")
//                                    .requestMatchers("swagger-ui/**").permitAll()
//                                    .requestMatchers("http://localhost:8080/swagger-ui/index.html").permitAll()
                                    .anyRequest().authenticated()
                    ).httpBasic(Customizer.withDefaults());
//            httpSecurity.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//            httpSecurity.authenticationProvider(authenticationProvider());
//            httpSecurity.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);


            return httpSecurity.build();


        }


}
