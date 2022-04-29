package com.example.idatt2106_2022_05_backend.config;

import com.example.idatt2106_2022_05_backend.security.JWTUtil;
import com.example.idatt2106_2022_05_backend.service.user.UserDetailsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
//import org.springframework.security.oauth2.jwt.JwtDecoder;
//import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketAuthenticationConfig implements WebSocketMessageBrokerConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketAuthenticationConfig.class);

    /*
    @Autowired
    private JwtDecoder jwtDecoder;
     */

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    List<String> authorization = accessor.getNativeHeader("Authorization");
                    logger.debug("Authorization: {}", authorization);

                    String accessToken = authorization.get(0).split(" ")[1];

                    String email = jwtUtil.getEmailFromToken(accessToken);

                    if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                        logger.info("set security context");

                        if (jwtUtil.validateToken(accessToken, userDetails)) {
                            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());

                            HttpServletRequest request =
                                    ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                                            .getRequest();

                            usernamePasswordAuthenticationToken
                                    .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

                            logger.info("Retrieving principal");
                            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                            logger.info("have authentication");
                            Principal myAuth = (Principal) authentication.getPrincipal();
                            logger.info("have principal");
                            accessor.setUser(myAuth);
                            //Not sure why, but necessary otherwise NPE in StompSubProtocolHandler!
                            accessor.setLeaveMutable(true);
                            logger.info("Message is: " + message.getPayload());
                        }

                        //Jwt jwt = jwtDecoder.decode(accessToken);


                        //JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
                        //Authentication authentication = converter.convert(jwt);

                        //accessor.setUser(authentication);

                        //accessor.setLeaveMutable(true);
                    }
                }
                return message;
            }
        });
    }
}

