package tests;

import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import models.Spell;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static io.restassured.RestAssured.*;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class SpellsTest {

    @BeforeAll
    static void config(){
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 3000;
        RestAssured.basePath = "/spells";
    }

    @BeforeEach
    void resetSpells(){
        //when().get("/actions/deleteAll").then().statusCode(200);
        //when().get("/actions/reset").then().statusCode(200);
    }

    @Test
    void itShouldReturnSpecificSpell() {
        given().pathParam("spellId", "5b74ee1d3228320021ab6239")
                .when().get("/{spellId}")
                .then().statusCode(200)
                .body("spell",equalTo("Avada Kedavra"))
                .body("effect",equalTo("murders opponent"))
                .body("type",equalTo("Curse"))
                .body("isUnforgivable", is(true));

    }

    @Test
    void itShouldReturnErrorMessageForWrongSpell() {
        given().pathParam("spellId", "5b74ee1d3228320021ab6239XXX")
                .when().get("/{spellId}")
                .then().statusCode(404)
                .body("message",equalTo("Spell not found"));
    }

    @Test
    void itShouldReturnSpellForEachSpellInList() {
        List<Spell> spells = when().get()
                .then().extract().response()
                .jsonPath().getList("$", Spell.class);

        spells.forEach(spell -> {
            assertThat(spell.getEffect() , not(emptyOrNullString()));
            assertThat(spell.getSpell() , not(emptyOrNullString()));
            assertThat(spell.getId() , not(emptyOrNullString()));
        });
    }

    @Test
    void itShouldReturnExactNumberOfSpells() {
        int size = when().get()
                .then().extract().response()
                .jsonPath().getList("$").size();
        System.out.println(size);
        assertThat(size, greaterThan(20));

    }

    @Test
    void itShouldContainOnlyCertainSpells() {
        List<String> desiredSpells = Arrays.asList("Imperio","Avada Kedavra","Crucio");
        List<String> spells = when().get()
                .then().statusLine(containsString("OK"))
                .extract().response()
                .jsonPath().getList("spell");

        for (String spell : spells) {
            for (String desiredSpell : desiredSpells) {
                if (spell.equals(desiredSpell)) {
                    System.out.println("Spell " + desiredSpell + " found.");
                }
            }
        }
    }

    @Test
    void checkStatusLineTest() {
        given().pathParam("spellId", "5b74ee1d3228320021ab6239")
                .when().get("/{spellId}")
                .then().statusLine(containsString("OK"));
    }

    @Test
    void filterStreamTest() {
        List<HashMap<Object,String>> spells = when().get().then().extract().jsonPath().getList("$");
        spells = spells.stream().filter(object -> object.get("type").equals("Charm")).collect(toList());
        System.out.println(spells);
    }

    @Test
    void itShouldFilterSpellBasedOnQueryType() {
        List<HashMap<Object, String>> spells = given().queryParam("type","Curse")
        .when().get().then().extract().jsonPath().getList("$");

        spells.forEach(spell -> assertThat(spell.get("type"), equalTo("Curse")));
        assertThat(spells, hasSize(greaterThan(10)));

    }

    @Test
    void itShouldAddNewSpell() {
        Faker faker = new Faker();

/*        HashMap<Object,Object> newSpell = new HashMap<>();
        newSpell.put("spell","Huhu".concat(faker.letterify("????????")));
        newSpell.put("effect","Makes you huhu");
        newSpell.put("type","Curse");
        newSpell.put("isUnforgivable","true");*/

        Spell spell = new Spell(
                "Huhu".concat(faker.letterify("????????")),
                "Curse",
                "Makes you Huhu",
                true
        );

        given().contentType(ContentType.JSON)
                //.header("Content-type", "application/json")
                .body(spell)
                .log().body()
                .when().post()
                .then()
                .statusCode(201)
                .log().body();
    }


}
