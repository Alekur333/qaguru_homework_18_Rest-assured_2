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
import static org.junit.jupiter.api.Assertions.assertEquals;

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
//                assertEquals($("#FirstNameme").getValue(), webShopConfig.userFirstName())
                        $("#FirstName").shouldHave(attribute("value", webShopConfig.userFirstName()))
        );

        step("Verify Last name is proper on profile page", () ->
                $("#LastName").shouldHave(attribute("value", webShopConfig.userLastName()))
        );


    }

    @Test
    @Tag("demoshop")
    @DisplayName("Pozitive registration of new user (API + UI)")
    void registrationTest() {

        String cookieRequest =
                "ARRAffinity=55622bac41413dfac968dd8f036553a9415557909fd0cd3244e7e0e656e4adc8; " +
                        "SL_GWPT_Show_Hide_tmp=1; " +
                        "SL_wptGlobTipTmp=1; " +
                        "__RequestVerificationToken=SYDtoaEM6fvlUBY_O5W4PBqvEvaUgAR_lYN0qgro6_b3qNSTVDL7VKMq78oycV2tH6vMNjN9hVJBpy43yZnm3KokdaKegg-Fl38pPF4BGzA1; " +
                        "ASP.NET_SessionId=aapdjfiaj0t3hztzzm2wyoiw; " +
                        "Nop.customer=036ba780-fbc6-4b77-acbf-2ea8c777c79a";

        String RequestVerificationToken = "rdkxJcr-fZ-fUpMCnt7BKVv9aCnIgGG-A3gu_NXjzMJZMf6iOH7vE0XjdyfL2QQU9kCeRGbg6E9YrCyfmhfeM4lKwlzvK1FqxwYGbAEElI41";

        step("Post registration of new user", () -> {

            String authorizationCookie =
                    given()
                            .contentType("application/x-www-form-urlencoded")
                            .cookie(cookieRequest)
                            .formParam("__RequestVerificationToken", RequestVerificationToken)
                            .formParam("FirstName", webShopConfig.userFirstName())
                            .formParam("LastName", webShopConfig.userLastName())
                            .formParam("Email", "4@al.kur")
                            .formParam("Password", "1@al.kur")
                            .formParam("ConfirmPassword", "1@al.kur")
                            .formParam("register-button", "Register")
                            .when()
                            .post("/register")
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
                        assertEquals($("#FirstName").getValue(), webShopConfig.userFirstName())
//                        $("#FirstName").shouldHave(attribute("value", webShopConfig.userFirstName()))
        );

        step("Verify Last name is proper on profile page", () ->
                $("#LastName").shouldHave(attribute("value", webShopConfig.userLastName()))
        );
    }
}
