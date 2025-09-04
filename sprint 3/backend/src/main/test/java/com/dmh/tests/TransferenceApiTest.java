package com.dmh.tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Pruebas automatizadas para el endpoint de ingresos (transferences) del Sprint 3.
 *
 * Estas pruebas ejercen distintos escenarios de creación de ingresos en una cuenta: casos
 * exitosos, montos no válidos, tarjetas inexistentes y verificación de requisitos de
 * autenticación. Los casos se ejecutan sobre el endpoint POST /accounts/{ID}/transferences.
 */
public class TransferenceApiTest extends BaseTest {

    @Test
    @DisplayName("Registrar ingreso con datos válidos devuelve 201")
    void createTransferenceValid() {
        int accountId = Integer.parseInt(System.getProperty("accountId", "1"));
        long cardId = Long.parseLong(System.getProperty("cardId", "1"));
        BigDecimal amount = new BigDecimal(System.getProperty("amount", "100.00"));
        Map<String, Object> body = new HashMap<>();
        body.put("cardId", cardId);
        body.put("amount", amount);
        body.put("description", System.getProperty("description", "Ingreso de prueba"));

        Response response = RestAssured.given()
                .header("Authorization", authToken.isEmpty() ? null : "Bearer " + authToken)
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/accounts/" + accountId + "/transferences");

        assertThat(response.getStatusCode(), is(201));
        // Validar que se devuelve un identificador de transacción
        assertThat(response.jsonPath().get("id"), notNullValue());
        assertThat(response.jsonPath().get("amount"), equalTo(amount.floatValue()));
    }

    @Test
    @DisplayName("Registrar ingreso con monto negativo devuelve 400")
    void createTransferenceInvalidAmount() {
        int accountId = Integer.parseInt(System.getProperty("accountId", "1"));
        long cardId = Long.parseLong(System.getProperty("cardId", "1"));
        Map<String, Object> body = new HashMap<>();
        body.put("cardId", cardId);
        body.put("amount", -50);
        body.put("description", "Monto negativo");

        Response response = RestAssured.given()
                .header("Authorization", authToken.isEmpty() ? null : "Bearer " + authToken)
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/accounts/" + accountId + "/transferences");

        assertThat(response.getStatusCode(), is(400));
    }

    @Test
    @DisplayName("Registrar ingreso con tarjeta inexistente devuelve 404")
    void createTransferenceNonExistingCard() {
        int accountId = Integer.parseInt(System.getProperty("accountId", "1"));
        long nonExistingCardId = Long.parseLong(System.getProperty("invalidCardId", "9999"));
        Map<String, Object> body = new HashMap<>();
        body.put("cardId", nonExistingCardId);
        body.put("amount", 100);
        body.put("description", "Tarjeta inexistente");

        Response response = RestAssured.given()
                .header("Authorization", authToken.isEmpty() ? null : "Bearer " + authToken)
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/accounts/" + accountId + "/transferences");

        assertThat(response.getStatusCode(), is(404));
    }

    @Test
    @DisplayName("Registrar ingreso sin token devuelve 401 o 403")
    void createTransferenceNoToken() {
        int accountId = Integer.parseInt(System.getProperty("accountId", "1"));
        long cardId = Long.parseLong(System.getProperty("cardId", "1"));
        Map<String, Object> body = new HashMap<>();
        body.put("cardId", cardId);
        body.put("amount", 100);
        body.put("description", "Sin token");

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/accounts/" + accountId + "/transferences");
        int status = response.getStatusCode();
        assertThat(status, anyOf(is(401), is(403)));
    }

    @Test
    @DisplayName("Registrar ingreso con token inválido devuelve 401 o 403")
    void createTransferenceInvalidToken() {
        int accountId = Integer.parseInt(System.getProperty("accountId", "1"));
        long cardId = Long.parseLong(System.getProperty("cardId", "1"));
        Map<String, Object> body = new HashMap<>();
        body.put("cardId", cardId);
        body.put("amount", 100);
        body.put("description", "Token inválido");

        Response response = RestAssured.given()
                .header("Authorization", "Bearer invalid-token")
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/accounts/" + accountId + "/transferences");
        int status = response.getStatusCode();
        assertThat(status, anyOf(is(401), is(403)));
    }
}