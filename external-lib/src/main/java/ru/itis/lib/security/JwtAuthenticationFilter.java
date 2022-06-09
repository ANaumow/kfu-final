package ru.itis.lib.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.annotation.PostConstruct;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

@Component("jwtAuthenticationFilter")
public class JwtAuthenticationFilter extends GenericFilterBean {

    @Value("${jwt.secret}")
    private String secret;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
//        HttpServletRequest request = (HttpServletRequest) servletRequest;
//
//        token = request.getHeader("Authorization");
//
//        try {
//            Jwts.parser()
//                .setSigningKey(secret)
//                .parseClaimsJws(token);
//        } catch (Exception e) {
//            throw new AuthenticationCredentialsNotFoundException("Bad token");
//        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    public static String token;


    @PostConstruct
    public void init() {
        String token = Jwts.builder()
                           .setSubject("eureka.instance-id") // id пользователя
                           .claim("name", "") // имя
                           .claim("role", "") // роль
                           .signWith(SignatureAlgorithm.HS256, secret) // подписываем его с нашим secret
                           .compact();

        System.out.println(123);
    }


}
