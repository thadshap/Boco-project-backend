package com.example.idatt2106_2022_05_backend.config;

import com.example.idatt2106_2022_05_backend.config.social.FacebookSignInAdapter;
import com.example.idatt2106_2022_05_backend.security.DatabaseLoginHandler;
import com.example.idatt2106_2022_05_backend.security.JWTConfig;
import com.example.idatt2106_2022_05_backend.security.oauth.OAuth2UserServiceImpl;
import com.example.idatt2106_2022_05_backend.security.oauth.OAuthLoginHandler;
import com.example.idatt2106_2022_05_backend.service.social.FacebookConnectionSignup;
import com.example.idatt2106_2022_05_backend.service.user.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.mem.InMemoryUsersConnectionRepository;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.social.connect.web.ProviderSignInController;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.web.cors.CorsConfiguration;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Configuration
@EnableWebSecurity
@ComponentScan
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsServiceImpl userService;

    @Autowired
    private JWTConfig jwtConfig;

    @Autowired
    private FacebookConnectionSignup facebookConnectionSignup;

//    @Value("${spring.social.facebook.client-secret}")
    String faceSecret = "822eef3823b53888eb4dd9f0c1a09463";

//    @Value("${spring.social.facebook.client-id}")
    String faceId = "1181763609285094";

//    @Value("${spring.social.google.client-secret}")
    String googleSecret;

//    @Value("${spring.social.google.client-id}")
    String googleId;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService);
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public ProviderSignInController providerSignInController() {
        ConnectionFactoryLocator connectionFactoryLocator =
                connectionFactoryLocator();
        UsersConnectionRepository usersConnectionRepository =
                getUsersConnectionRepository(connectionFactoryLocator);
        ((InMemoryUsersConnectionRepository) usersConnectionRepository)
                .setConnectionSignUp(facebookConnectionSignup);
        return new ProviderSignInController(connectionFactoryLocator,
                usersConnectionRepository, new FacebookSignInAdapter());
    }

    private ConnectionFactoryLocator connectionFactoryLocator() {
        ConnectionFactoryRegistry registry = new ConnectionFactoryRegistry();
        registry.addConnectionFactory(new FacebookConnectionFactory(faceId, faceSecret));
        return registry;
    }

    private UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator
                                                                           connectionFactoryLocator) {
        return new InMemoryUsersConnectionRepository(connectionFactoryLocator);
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
                .antMatchers("/login*","/signin/**","/signup/**").permitAll()
                .antMatchers("/**", "/auth/login", "/h2/**", "/auth/login/outside/service", "/auth/forgotPassword")
                .permitAll().antMatchers("/v2/api-docs").permitAll().antMatchers("/configuration/ui").permitAll()
                .antMatchers("/swagger-resources/**").permitAll().antMatchers("/configuration/security").permitAll()
                .antMatchers("/swagger-ui.html").permitAll().antMatchers("/swagger-ui/**").permitAll()
                .antMatchers(HttpMethod.POST, "/auth/**").permitAll().antMatchers(HttpMethod.GET, "/auth/**")
                .permitAll().antMatchers(HttpMethod.POST, "/users/").permitAll()
                .antMatchers(HttpMethod.GET, "/users/**").permitAll().antMatchers(HttpMethod.POST, "/courses/**")
                .permitAll().anyRequest().authenticated()
                .and()
                .formLogin().loginPage("/login").permitAll()
//                .usernameParameter("email")
//                .passwordParameter("pass")
//                .successHandler(databaseLoginHandler)
//                .and()
//                .oauth2Login().loginPage("/auth/login/outside/service").userInfoEndpoint()
//                .userService(oauth2UserService).and().successHandler(oauthLoginHandler)
                .and()
                .logout()
                .logoutSuccessUrl("/").permitAll().and().exceptionHandling().authenticationEntryPoint((req, res, e) -> {
                    res.setContentType("application/json");
                    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    res.getOutputStream().println("{ \"message\": \"Tilgang er ikke gitt.\"}");
                }).and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        httpSecurity.headers().frameOptions().disable();
//        httpSecurity.addFilterBefore(jwtConfig, UsernamePasswordAuthenticationFilter.class);
    }

    @Autowired
    private OAuth2UserServiceImpl oauth2UserService;

    @Autowired
    private OAuthLoginHandler oauthLoginHandler;

    @Autowired
    private DatabaseLoginHandler databaseLoginHandler;
}
