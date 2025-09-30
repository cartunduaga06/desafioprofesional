package com.dmh.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.List;

@Component
public class JwtAuthenticationFilter implements WebFilter {

  private final Key key;
  private final AntPathMatcher matcher = new AntPathMatcher();
  private final List<String> publicPaths = List.of(
      "/auth/login",
      "/users/register",
      "/swagger-ui/**",
      "/v3/api-docs/**",
      "/actuator/health"
  );

  public JwtAuthenticationFilter(@Value("${jwt.secret}") String base64Secret) {
    byte[] decoded = Base64.getDecoder().decode(base64Secret);
    this.key = Keys.hmacShaKeyFor(decoded);
  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    ServerHttpRequest request = exchange.getRequest();

    // allow public paths
    String path = request.getURI().getPath();
    for (String pattern : publicPaths) {
      if (matcher.match(pattern, path)) {
        return chain.filter(exchange);
      }
    }

    List<String> authHeaders = request.getHeaders().getOrEmpty("Authorization");
    if (authHeaders.isEmpty() || !authHeaders.get(0).startsWith("Bearer ")) {
      exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
      return exchange.getResponse().setComplete();
    }

    String token = authHeaders.get(0).substring(7);

    try {
      Claims claims = Jwts.parserBuilder()
          .setSigningKey(key)
          .build()
          .parseClaimsJws(token)
          .getBody();

      String subject = claims.getSubject(); // email
      Object idClaim = claims.get("id");

      // propagate downstream
      ServerHttpRequest mutated = exchange.getRequest().mutate()
          .header("X-User-Email", subject != null ? subject : "")
          .header("X-User-Id", idClaim != null ? String.valueOf(idClaim) : "")
          .build();

      return chain.filter(exchange.mutate().request(mutated).build());
    } catch (Exception e) {
      exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
      return exchange.getResponse().setComplete();
    }
  }
}
