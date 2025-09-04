package com.dmh.tests;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;

/**
 * BaseTest configura la URI base y encabezados comunes para las pruebas.
 * La URL base puede ser sobreescrita mediante la propiedad del sistema 'baseUrl'.
 * La autenticación se gestiona mediante un token Bearer proporcionado en la propiedad 'authToken'.
 */
public abstract class BaseTest {

    protected static String authToken;

    @BeforeAll
    public static void setup() {
        // Configurar la URI base a partir de variable del sistema o default
        String baseUrl = System.getProperty("baseUrl", "http://localhost:8080");
        RestAssured.baseURI = baseUrl;

        // Leer token de autenticación para las pruebas, opcional
        authToken = System.getProperty("authToken", "");
    }
}