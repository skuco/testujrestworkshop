package tests;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.*;
import static org.hamcrest.core.IsEqual.equalTo;

public class CharacterTest {

    @BeforeAll
    static void config(){
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 3000;
        RestAssured.basePath = "/characters";
    }

    @Test
    void itShouldReturnErrorMessageWhenUserIsNotAuthenticated() {
        when().get()
                .then()
                .statusCode(401)
                .body("message", equalTo("Sorry Wizard, can't let you in."));
    }

    @Test
    void itShouldReturnErrorMessageWhenUserIsAuthenticated() {
        given()
                .auth().preemptive().basic("admin","supersecret")
                .when()
                .get().then().log().body().statusCode(200);
    }
}
