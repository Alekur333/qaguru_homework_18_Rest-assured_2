package kur.alex.tests.demoshop;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import io.restassured.RestAssured;
import kur.alex.config.AppConfig;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.restassured.RestAssured.given;
import static io.qameta.allure.Allure.step;

public class DemoShopTests {

    public static AppConfig webShopConfig = ConfigFactory.create(AppConfig.class, System.getProperties());

    @BeforeAll
    static void configureBaseUrl() {
        RestAssured.baseURI = webShopConfig.apiUrl();
        Configuration.baseUrl = webShopConfig.webUrl();
//        SelenideLogger.addListener("AllureSelenide", new AllureSelenide());

    }

    @Test
    @Tag("demoshop")
    @DisplayName("Successful users name (API + UI)")
    void loginWithCookieToCheckUsersNamesTest() {

        step("Get cookie by api and set it to browser", () -> {
            String authorizationCookie =
                    given()
                            .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                            .formParam("Email", webShopConfig.userLogin())
                            .formParam("Password", webShopConfig.userPassword())
                            .when()
                            .post("/login")
                            .then()
                            .statusCode(302)
                            .extract()
                            .cookie("NOPCOMMERCE.AUTH");

            step("Open minimal content, because cookie can be set when site is opened", () ->
                    open("/Themes/DefaultClean/Content/images/logo.png"));


            step("Set cookie to browser", () ->

                    getWebDriver().manage().addCookie(
                            new Cookie("NOPCOMMERCE.AUTH", authorizationCookie)));
        });

        step("Open profile page", () ->
                open("/customer/info"));

        step("Verify First name is proper on profile page", () ->
//                $("#FirstName").shouldHave(text(webShopConfig.userFirstName()))
                $("#FirstName").shouldHave(attribute("value", webShopConfig.userFirstName()))
        );

        step("Verify Last name is proper on profile page", () ->
                $("#LastName").shouldHave(attribute("value", webShopConfig.userLastName()))
        );


    }
}
