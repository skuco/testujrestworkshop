package tests;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class SpellsTest {

    @BeforeAll
    static void config(){
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 3000;
        RestAssured.basePath = "/spells";
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
        List<HashMap<Object , Object>> spells =
        when().get()
                .then().extract().response()
                .jsonPath().getList("$");
        spells.forEach(spell -> {
            assertThat(spell.get("effect").toString() , not(emptyOrNullString()));
            assertThat(spell.get("spell").toString() , not(emptyOrNullString()));
            assertThat(spell.get("id").toString() , not(emptyOrNullString()));
        });
    }

    @Test
    void itShouldReturnExactNumberOfSpells() {
        int size = when().get()
                .then().extract().response()
                .jsonPath().getList("$").size();
        System.out.println(size);
        assertThat(size, is(151));

    }
}
