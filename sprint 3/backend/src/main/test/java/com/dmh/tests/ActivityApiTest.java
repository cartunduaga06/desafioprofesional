package com.dmh.tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Pruebas automatizadas para los nuevos endpoints de actividad en Sprint 3.
 *
 * Estas pruebas se centran en la consulta del historial de movimientos de una cuenta y
 * la obtención del detalle de una transacción específica. Se consideran escenarios
 * positivos y negativos relacionados con la autenticación y la existencia de recursos.
 */
public class ActivityApiTest extends BaseTest {

    @Test
    @DisplayName("Listar actividad con token válido devuelve 200")
    void listActivityValid() {
        int accountId = Integer.parseInt(System.getProperty("accountId", "1"));
        Response response = RestAssured.given()
                .header("Authorization", authToken.isEmpty() ? null : "Bearer " + authToken)
                .when()
                .get("/accounts/" + accountId + "/activity");

        assertThat(response.getStatusCode(), is(200));
        // Verificar que se devuelve una lista (puede estar vacía si no hay movimientos)
        assertThat(response.jsonPath().getList("."), notNullValue());
    }

    @Test
    @DisplayName("Listar actividad sin token devuelve 401 o 403")
    void listActivityNoToken() {
        int accountId = Integer.parseInt(System.getProperty("accountId", "1"));
        Response response = RestAssured
                .when()
                .get("/accounts/" + accountId + "/activity");

        int status = response.getStatusCode();
        assertThat(status, anyOf(is(401), is(403)));
    }

    @Test
    @DisplayName("Listar actividad con token inválido devuelve 401 o 403")
    void listActivityInvalidToken() {
        int accountId = Integer.parseInt(System.getProperty("accountId", "1"));
        Response response = RestAssured.given()
                .header("Authorization", "Bearer invalid-token")
                .when()
                .get("/accounts/" + accountId + "/activity");
        int status = response.getStatusCode();
        assertThat(status, anyOf(is(401), is(403)));
    }

    @Test
    @DisplayName("Obtener transacción existente devuelve 200")
    void getTransactionValid() {
        int accountId = Integer.parseInt(System.getProperty("accountId", "1"));
        int transactionId = Integer.parseInt(System.getProperty("transactionId", "1"));

        Response response = RestAssured.given()
                .header("Authorization", authToken.isEmpty() ? null : "Bearer " + authToken)
                .when()
                .get("/accounts/" + accountId + "/activity/" + transactionId);
        assertThat(response.getStatusCode(), is(200));
        // Validar que se devuelven campos de la transacción (por ejemplo, id y amount)
        assertThat(response.jsonPath().get("id"), notNullValue());
        assertThat(response.jsonPath().get("amount"), notNullValue());
    }

    @Test
    @DisplayName("Obtener transacción inexistente devuelve 404")
    void getTransactionNotFound() {
        int accountId = Integer.parseInt(System.getProperty("accountId", "1"));
        long nonExistingId = Long.parseLong(System.getProperty("invalidTransactionId", "9999"));

        Response response = RestAssured.given()
                .header("Authorization", authToken.isEmpty() ? null : "Bearer " + authToken)
                .when()
                .get("/accounts/" + accountId + "/activity/" + nonExistingId);
        assertThat(response.getStatusCode(), is(404));
    }

    @Test
    @DisplayName("Obtener transacción sin token devuelve 401 o 403")
    void getTransactionNoToken() {
        int accountId = Integer.parseInt(System.getProperty("accountId", "1"));
        int transactionId = Integer.parseInt(System.getProperty("transactionId", "1"));

        Response response = RestAssured
                .when()
                .get("/accounts/" + accountId + "/activity/" + transactionId);
        int status = response.getStatusCode();
        assertThat(status, anyOf(is(401), is(403)));
    }
}