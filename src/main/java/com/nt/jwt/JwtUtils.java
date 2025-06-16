package com.nt.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    @Value("${spring.app.jwtExpirationInMs}")
    private String jwtExpirationInMs;

    private SecretKey secretKey;

    @Value("${spring.app.jwtCookie}")
    private String jwtCookie;

    @PostConstruct
    public void init() {
        // Initialize the key once during bean creation
        this.secretKey = Jwts.SIG.HS256.key().build();
    }

    // Getting JwtToken From The Header.
    /*
    public String getJwtFromHeader(HttpServletRequest request) {

        String bearerToken = request.getHeader("Authorization");
        if(bearerToken != null && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }
        return null;
    }

     */

    // Getting JwtToken From The Cookie.
    public String getJwtFromCookies(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, jwtCookie);
        if(cookie != null){
            return cookie.getValue();
        }
        return null;
    }

    // Getting userName From The JwtToken.
    public String gettingUserNameFromJwtToken(String token){
        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build()
                .parseSignedClaims(token)
                .getPayload().getSubject();

    }
    // Generate jwtCookie  From UserName.
    public ResponseCookie generateJwtCookie(UserDetails userDetails){
        String jwt = generateTokenFromUserName(userDetails);
        ResponseCookie cookie = ResponseCookie.from(jwtCookie, jwt)
                .path("/api")
                .maxAge(24*60*60)
                .httpOnly(false)
                .build();

        return cookie;
    }

    // Getting Cleaned JwtCookie.for Logout.
    public ResponseCookie getCleanedJwtCookie(){

        ResponseCookie cookie = ResponseCookie.from(jwtCookie, null)
                .path("/api")
                .build();

        return cookie;
    }


    // Generate Token From UserName.
    public String generateTokenFromUserName(UserDetails userDetails) {
        String userName = userDetails.getUsername();
        return Jwts.builder()
                .subject(userName)
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + Long.parseLong(jwtExpirationInMs)))
                .signWith(key())
                .compact();

    }

    // Generating Signing Key.
    public Key key() {
        // Return the pre-generated key
        return secretKey;
    }

    // Validating Jwt Token.
    public boolean validateJwtToken(String jwtToken){
        try{
             Jwts.parser().verifyWith((SecretKey) key()).build().parseSignedClaims(jwtToken);
             return true;
         }catch (MalformedJwtException e){

         }catch (ExpiredJwtException e){

        }catch (UnsupportedJwtException e){

        }catch (IllegalArgumentException e){

        }
        return false;
    }
}
