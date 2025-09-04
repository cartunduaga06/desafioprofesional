package com.dmh.tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Pruebas de smoke para los endpoints de cuenta y movimientos en Digital Money House.
 */
public class AccountApiTest extends BaseTest {

    @Test
    @DisplayName("Consultar cuenta con ID válido devuelve 200 y datos de cuenta")
    void getAccountValid() {
        int accountId = Integer.parseInt(System.getProperty("accountId", "1"));

        Response response = RestAssured.given()
                .header("Authorization", authToken.isEmpty() ? null : "Bearer " + authToken)
                .when()
                .get("/accounts/" + accountId);

        assertThat(response.getStatusCode(), is(200));
        assertThat(response.jsonPath().get("cvu"), notNullValue());
        assertThat(response.jsonPath().get("alias"), notNullValue());
        assertThat(response.jsonPath().get("availableAmount"), notNullValue());
    }

    @Test
    @DisplayName("Consultar movimientos devuelve como máximo cinco transacciones")
    void getLastTransactions() {
        int accountId = Integer.parseInt(System.getProperty("accountId", "1"));

        Response response = RestAssured.given()
                .header("Authorization", authToken.isEmpty() ? null : "Bearer " + authToken)
                .when()
                .get("/accounts/" + accountId + "/transactions");

        assertThat(response.getStatusCode(), is(200));
        // Verificar que la lista tiene como máximo 5 elementos
        int size = response.jsonPath().getList(".").size();
        assertThat(size, lessThanOrEqualTo(5));
    }
}