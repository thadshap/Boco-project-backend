package com.example.idatt2106_2022_05_backend.config;

import com.example.idatt2106_2022_05_backend.security.JWTUtil;
import com.example.idatt2106_2022_05_backend.service.user.UserDetailsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import springfox.documentation.spi.service.contexts.SecurityContext;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Configuration
@EnableScheduling
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    private Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");// This is the endpoint where clients can subscribe
        config.setApplicationDestinationPrefixes("/app");// Endpoint passes messages to endpoints in controllers, using
                                                         // message mapping annotation
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws");
        // addEndpoint is the endpoint where clients requests connection, handshake happens here
        registry.addEndpoint("/ws").setAllowedOrigins("https://localhost:8080", "http://apic.app/online/",
                "chrome-extension://ggnhohnkfcpcanfekomdkjffnfcjnjam").withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);
                logger.debug("in client inbound channel");
                logger.debug(headerAccessor.getCommand().toString());
                logger.debug(message.toString());

                if (StompCommand.CONNECT.equals(headerAccessor.getCommand())) {
                    logger.debug("inside if");

                    List<String> authorization = headerAccessor.getNativeHeader("Authorization");
                    logger.debug("Authorization: " + authorization);

                    String accessToken = authorization.get(0).split(" ")[1];

                    String email = jwtUtil.getEmailFromToken(accessToken);

                    if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                        logger.debug("set security context");

                        if (jwtUtil.validateToken(accessToken, userDetails)) {
                            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());

                            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                                    .getRequestAttributes()).getRequest();

                            usernamePasswordAuthenticationToken
                                    .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

                            logger.debug("Retrieving principal");
                            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                            logger.debug("have authentication");
                            Principal myAuth = (Principal) authentication.getPrincipal();
                            logger.debug("have principal");
                            headerAccessor.setUser(myAuth);
                            // Not sure why, but necessary otherwise NPE in StompSubProtocolHandler!
                            headerAccessor.setLeaveMutable(true);
                            logger.debug("Message is: " + message.getPayload());
                        }

                        // Jwt jwt = jwtDecoder.decode(accessToken);

                        // JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
                        // Authentication authentication = converter.convert(jwt);

                        // accessor.setUser(authentication);

                        // accessor.setLeaveMutable(true);
                    }
                }

                return message;
            }
        });
    }

    /*
     * @Override public void configureClientInboundChannel(ChannelRegistration registration) {
     * registration.interceptors(new ChannelInterceptor() {
     * 
     * @Override public Message<?> preSend(Message<?> message, MessageChannel channel) {
     * logger.info("recieved a message"); StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);
     * List<String> tokenList = headerAccessor.getNativeHeader("Authorization"); logger.info("removing header");
     * logger.info("Message: " + message.getPayload().toString() + message.toString());
     * headerAccessor.removeHeader("Authorization");
     * 
     * String email = null; String token = null;
     * 
     * if ( tokenList!=null && tokenList.size() > 0) { String auth = tokenList.get(0);
     * logger.info("auth looks like this: " + auth);
     * 
     * if ( auth.startsWith("Bearer ")) { token = auth.substring(7); email = jwtUtil.getEmailFromToken(token); }
     * 
     * if (email!=null && SecurityContextHolder.getContext().getAuthentication() == null) { UserDetails userDetails =
     * userDetailsService.loadUserByUsername(email);
     * 
     * logger.info("set security context");
     * 
     * if (jwtUtil.validateToken(token, userDetails)) { UsernamePasswordAuthenticationToken
     * usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken( userDetails, null,
     * userDetails.getAuthorities());
     * 
     * HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes())
     * .getRequest();
     * 
     * usernamePasswordAuthenticationToken .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
     * 
     * SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
     * 
     * logger.info("Retrieving principal"); Authentication authentication =
     * SecurityContextHolder.getContext().getAuthentication(); Principal myAuth = (Principal)
     * authentication.getPrincipal(); headerAccessor.setUser(myAuth); //Not sure why, but necessary otherwise NPE in
     * StompSubProtocolHandler! headerAccessor.setLeaveMutable(true); logger.info("Message is: "+ message.getPayload());
     * 
     * return MessageBuilder.createMessage(message.getPayload(), headerAccessor.getMessageHeaders()); } }
     * 
     * } return message; } }); }
     * 
     */

}