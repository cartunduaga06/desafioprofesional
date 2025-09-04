package com.dmh.tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Pruebas de smoke para la gestión de tarjetas.
 */
public class CardApiTest extends BaseTest {

    @Test
    @DisplayName("Listar tarjetas de una cuenta devuelve 200")
    void listCards() {
        int accountId = Integer.parseInt(System.getProperty("accountId", "1"));
        Response response = RestAssured.given()
                .header("Authorization", authToken.isEmpty() ? null : "Bearer " + authToken)
                .when()
                .get("/accounts/" + accountId + "/cards");
        assertThat(response.getStatusCode(), is(200));
    }

    @Test
    @DisplayName("Crear tarjeta válida devuelve 201 y número de tarjeta")
    void createCard() {
        int accountId = Integer.parseInt(System.getProperty("accountId", "1"));
        Map<String, Object> body = new HashMap<>();
        body.put("number", System.getProperty("cardNumber", "5555666677778888"));
        body.put("holderName", System.getProperty("cardHolder", "Juan Perez"));
        body.put("expiryMonth", System.getProperty("expiryMonth", "12"));
        body.put("expiryYear", System.getProperty("expiryYear", "29"));
        body.put("cvv", System.getProperty("cvv", "123"));

        Response response = RestAssured.given()
                .header("Authorization", authToken.isEmpty() ? null : "Bearer " + authToken)
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/accounts/" + accountId + "/cards");

        assertThat(response.getStatusCode(), is(201));
        assertThat(response.jsonPath().get("cardId"), notNullValue());
    }

    @Test
    @DisplayName("Eliminar tarjeta existente devuelve 200")
    void deleteCard() {
        int accountId = Integer.parseInt(System.getProperty("accountId", "1"));
        int cardId = Integer.parseInt(System.getProperty("cardId", "1"));
        Response response = RestAssured.given()
                .header("Authorization", authToken.isEmpty() ? null : "Bearer " + authToken)
                .when()
                .delete("/accounts/" + accountId + "/cards/" + cardId);
        assertThat(response.getStatusCode(), is(200));
    }
}