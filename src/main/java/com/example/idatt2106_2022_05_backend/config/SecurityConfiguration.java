package com.example.idatt2106_2022_05_backend.config;

import com.example.idatt2106_2022_05_backend.security.DatabaseLoginHandler;
import com.example.idatt2106_2022_05_backend.security.JWTConfig;
import com.example.idatt2106_2022_05_backend.security.oauth.OAuth2UserServiceImpl;
import com.example.idatt2106_2022_05_backend.security.oauth.OAuthLoginHandler;
import com.example.idatt2106_2022_05_backend.service.user.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsServiceImpl userService;

    @Autowired
    private JWTConfig jwtConfig;

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
            cors.setAllowedOrigins(List.of("*"));
            cors.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
            cors.setAllowedHeaders(List.of("*"));
            return cors;
        }).and().csrf().disable().authorizeRequests()
                // Allow anonymous access to websocket
                .antMatchers("/ws/**").permitAll()

                .antMatchers("/**", "/auth/login", "/h2/**", "/auth/login/outside/service", "/auth/forgotPassword")
                .permitAll().antMatchers("/v2/api-docs").permitAll().antMatchers("/configuration/ui").permitAll()
                .antMatchers("/swagger-resources/**").permitAll().antMatchers("/configuration/security").permitAll()
                .antMatchers("/swagger-ui.html").permitAll().antMatchers("/swagger-ui/**").permitAll()
                .antMatchers(HttpMethod.POST, "/auth/**").permitAll().antMatchers(HttpMethod.GET, "/auth/**")
                .permitAll().antMatchers(HttpMethod.POST, "/users/").permitAll()
                .antMatchers(HttpMethod.GET, "/users/**").permitAll().antMatchers(HttpMethod.POST, "/courses/**")
                .permitAll().anyRequest().authenticated()
                .and()

                // Relax CSRF on the WebSocket due to needing direct access from apps
                .csrf().ignoringAntMatchers("/ws/**").and()

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
        //httpSecurity.addFilterBefore(jwtConfig, UsernamePasswordAuthenticationFilter.class);
    }

    @Autowired
    private OAuth2UserServiceImpl oauth2UserService;

    @Autowired
    private OAuthLoginHandler oauthLoginHandler;

    @Autowired
    private DatabaseLoginHandler databaseLoginHandler;
}
