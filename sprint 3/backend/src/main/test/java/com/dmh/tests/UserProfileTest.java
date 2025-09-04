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
 * Pruebas para el endpoint de perfil de usuario.
 */
public class UserProfileTest extends BaseTest {

    @Test
    @DisplayName("Consultar perfil devuelve 200 y datos del usuario")
    void getUserProfile() {
        int userId = Integer.parseInt(System.getProperty("userId", "1"));
        Response response = RestAssured.given()
                .header("Authorization", authToken.isEmpty() ? null : "Bearer " + authToken)
                .when()
                .get("/users/" + userId);
        assertThat(response.getStatusCode(), is(200));
        assertThat(response.jsonPath().get("id"), notNullValue());
        assertThat(response.jsonPath().get("email"), notNullValue());
    }

    @Test
    @DisplayName("Actualizar perfil con datos válidos devuelve 200")
    void updateUserProfile() {
        int userId = Integer.parseInt(System.getProperty("userId", "1"));
        Map<String, Object> body = new HashMap<>();
        body.put("firstname", System.getProperty("firstname", "NombreModificado"));
        body.put("lastname", System.getProperty("lastname", "ApellidoModificado"));

        Response response = RestAssured.given()
                .header("Authorization", authToken.isEmpty() ? null : "Bearer " + authToken)
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .patch("/users/" + userId);
        assertThat(response.getStatusCode(), is(200));
    }
}