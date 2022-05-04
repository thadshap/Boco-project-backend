package com.example.idatt2106_2022_05_backend.config;

//import com.example.idatt2106_2022_05_backend.security.DatabaseLoginHandler;
import com.example.idatt2106_2022_05_backend.security.JWTConfig;
//import com.example.idatt2106_2022_05_backend.security.oauth.OAuth2UserServiceImpl;
//import com.example.idatt2106_2022_05_backend.security.oauth.OAuthLoginHandler;
import com.example.idatt2106_2022_05_backend.service.user.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Configuration
@EnableWebSecurity
@Order(1)
@ComponentScan
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsServiceImpl userService;

    @Autowired
    private JWTConfig jwtConfig;

    private static final String[] WHITELIST_URLS = {
            "/**",
            "/auth/**",
            "/ws",
            "/ws/**",
            "/api/ads/**",
            "/api/ads/**",
            "/api/users/**",
            "/api/search/**",
            "/api/sort/**",
            "/api/filterByDistance",
            "/api/getListWithinPriceRange",
            "/api/categories/**",
            "/api/categoriesRecursive/**",
            "/api/calendar/**",
            "/api/reviews/**"
    };

    private static final String[] WHITELIST_DOCS = {
            "/h2/**",
            "/v2/api-docs",
            "/configuration/ui",
            "/swagger-resources/**",
            "/configuration/security",
            "/swagger-ui.html",
            "/swagger-ui/**"
    };

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService);
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.cors().configurationSource(request -> {
            var cors = new CorsConfiguration();
            cors.setAllowCredentials(true);
            cors.setAllowedOrigins(List.of("http://localhost:8080/", "chrome-extension://ggnhohnkfcpcanfekomdkjffnfcjnjam"));
            cors.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
            cors.setAllowedHeaders(List.of("*"));
            return cors;
        }).and().csrf().disable().authorizeRequests()
                .antMatchers(WHITELIST_DOCS).permitAll()
                .antMatchers(WHITELIST_URLS).permitAll()
//                .antMatchers(HttpMethod.POST, "/user/").permitAll()
                .antMatchers(HttpMethod.GET, "/user/**").permitAll()
//                .antMatchers(HttpMethod.POST, "/courses/**").permitAll()
                .anyRequest().authenticated()
//                .and()
//                .x509()
//                .subjectPrincipalRegex("CN=(.*?)(?:,|$)")
                .and()

                // Relax CSRF on the WebSocket due to needing direct access from apps
                //.csrf().ignoringAntMatchers("/ws/**").and()
                //.authorizeHttpRequests().antMatchers("/ws/**").permitAll().and()

//                .formLogin().permitAll().loginPage("/auth/login")
//                .usernameParameter("email").passwordParameter("password").successHandler(databaseLoginHandler).and()
//                .oauth2Login().loginPage("/auth/login/outside/service").userInfoEndpoint()
//                .userService(oauth2UserService).and().successHandler(oauthLoginHandler)
//                .and()
                .logout()
                .logoutSuccessUrl("/").permitAll().and().exceptionHandling().authenticationEntryPoint((req, res, e) -> {
                    res.setContentType("application/json");
                    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    res.getOutputStream().println("{ \"message\": \"Tilgang er ikke gitt.\"}");
                }).and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        httpSecurity.headers().frameOptions().disable();
        httpSecurity.addFilterBefore(jwtConfig, UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("static/**");
    }
}
