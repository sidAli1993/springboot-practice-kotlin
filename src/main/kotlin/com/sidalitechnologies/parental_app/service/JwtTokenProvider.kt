package com.sidalitechnologies.parental_app.service

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

@Component
class JwtTokenProvider(
    private val userDetailsService: CustomUserDetailsServiceImpl,
    @Value("\${jwt.secret}") private val jwtSecret: String,
    @Value("\${jwt.expiration}") private val jwtExpiration: Long
) {

    private val secretKey: SecretKey = SecretKeySpec(jwtSecret.toByteArray(), SignatureAlgorithm.HS512.jcaName)

    fun generateToken(authentication: Authentication): String {
        val now = Date()
        val expiryDate = Date(now.time + jwtExpiration)

        return Jwts.builder()
            .setSubject(authentication.name)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(secretKey, SignatureAlgorithm.HS512)
            .compact()
    }
    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
            true
        } catch (ex: Exception) {
            false  // Token is invalid
        }
    }

    // Method to get Authentication from a JWT token
    fun getAuthentication(token: String): Authentication {
        val username = getUsernameFromToken(token)
        val userDetails: UserDetails = userDetailsService.loadUserByUsername(username)
        return UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
    }

    // Helper method to extract the username from the token
    private fun getUsernameFromToken(token: String): String {
        val claims: Claims = Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body
        return claims.subject
    }
}