package tests;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.codeborne.selenide.Condition.empty;
import static io.restassured.RestAssured.given;
import static java.time.LocalDate.now;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;

public class KiwiWorkshopTest {

    private static final String BASE_URL = "https://api.skypicker.com/";

    @BeforeAll
    static void config(){
        RestAssured.baseURI = BASE_URL;
    }

    @Test
    void itShouldReturnAllStationsInPrague() {
        String[] expectedStations = new String[]{
                "Praha hlavní nádraží",
                "Praha - Masarykovo ",
                "Praha-Kbely",
                "Praha-Smíchov ",
                "Prague Holesovice (Praha)"
        };
        given().queryParam("term", "Praha")
                .queryParam("location_types" , "station")
                .when().get("/locations")
                .then().statusCode(200)
                //.extract().response().prettyPrint();
        .body("locations.name", hasItems(expectedStations));
    }

    @Test
    void itShouldListAllSpecialPlacesInTokio() {
        List<String> specialLocations = given().queryParam("term", "Bratislava")
                .queryParam("location_types" , "airport")
                .queryParam("limit" , 1)
                .when().get("/locations")
                .then().statusCode(200).extract().jsonPath().getList("locations[0].special.name");

        System.out.println(specialLocations);
        specialLocations.forEach(System.out::println);
        assertThat(specialLocations, is(not(empty)));

    }

    @Test
    void itShouldReturnFlight() {
        given().queryParam("flyFrom", getIdOfCity("prague"))
                .queryParam("to" , getIdOfCity("new york"))
                .queryParam("dateFrom" , getDateWithOffset(1))
                .queryParam("dateTo" , getDateWithOffset(30))
                .queryParam("partner", "picky")
                .when().get("/flights")
                .then().log().body().statusCode(200).time(lessThan(6000L));
    }

    private static String getDateWithOffset(int offset) {
        return now()
                .plusDays(offset)
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    private String getIdOfCity(String term) {
        return given().baseUri(BASE_URL).queryParam("term", term)
                .queryParam("location_types", "airport")
                .queryParam("limit", 1)
                .queryParam("active_only", true)
                .when().get("/locations")
                .then().statusCode(200)
                .extract().path("locations[0].id");
    }
}
