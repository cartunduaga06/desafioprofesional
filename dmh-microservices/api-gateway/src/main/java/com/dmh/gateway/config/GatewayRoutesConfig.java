package com.dmh.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutesConfig {

  @Value("${dmh.users.serviceId:USER-SERVICE}")
  private String usersService;

  @Value("${dmh.accounts.serviceId:ACCOUNT-SERVICE}")
  private String accountsService;

  @Value("${dmh.cards.serviceId:CARD-SERVICE}")
  private String cardsService;

  @Value("${dmh.transactions.serviceId:TRANSACTION-SERVICE}")
  private String txService;

  @Bean
  public RouteLocator routes(RouteLocatorBuilder builder) {
    return builder.routes()
      .route("users", r -> r.path("/users/**", "/auth/**").uri("lb://" + usersService))
      .route("accounts", r -> r.path("/accounts/**").uri("lb://" + accountsService))
      .route("cards", r -> r.path("/cards/**").uri("lb://" + cardsService))
      .route("transactions", r -> r.path("/transactions/**").uri("lb://" + txService))
      .build();
  }
}
