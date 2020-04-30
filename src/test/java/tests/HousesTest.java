package tests;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.*;
import static org.hamcrest.core.IsEqual.equalTo;

public class HousesTest {

    @BeforeAll
    static void config(){
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 3000;
        //RestAssured.basePath = "/houses";
    }
    @Test
    void itShouldReturnMessageWithoutToken() {
        when().get("/houses")
                .then()
                .statusCode(403)
                .body("message", equalTo("Sorry Wizard you dont have TOKEN"));
    }

    @Test
    void itShouldReturnHousesWhenUserHasToken() {
        //1 - get token
        String token = given()
                            .auth().preemptive().basic("admin","supersecret")
                            .when().get("/login")
                            .then().extract().jsonPath().get("token");
        System.out.println(token);
        //2 - use token
        given().header("Authorization","Bearer ".concat(token))
                .when().get("/houses").then().log().body().statusCode(200);
    }
}
