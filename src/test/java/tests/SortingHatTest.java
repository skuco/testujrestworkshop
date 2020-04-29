package tests;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.*;


public class SortingHatTest {

    @BeforeAll
    static void config(){
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 3000;
        RestAssured.basePath = "sortingHat";
    }

    @Test
    void itShouldReturnStatusCode200() {
        when().get()
                .then().statusCode(200);
    }

    @Test
    void itShouldReturnCorrectKeys() {
        when().get()
                .then().log().body()
                .and().body("",hasKey("sortingHatSays"))
                .and().body("",hasKey("house"));
    }

    @Test
    void itShouldContainOneOfHouses() {
        when().get()
                .then().log().body()
                .body("house", oneOf("Slytherin" , "Ravenclaw" , "Hufflepuff" , "Gryffindor"));
    }

    @Test
    void itShouldContainMessageFromSortingHat() {
        when().get()
                .then().log().body()
                .body("sortingHatSays" , not(emptyOrNullString()));
    }
}
