package refecSuite;

import static io.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

import core.BaseTest;
import io.restassured.RestAssured;
import refac.AuthTest;
import refac.ContasTest;
import refac.MovimentacaoTest;
import refac.SaldoTest;

@RunWith(org.junit.runners.Suite.class)
@SuiteClasses({
	ContasTest.class,
	MovimentacaoTest.class,
	SaldoTest.class,
	AuthTest.class,
})
public class Suite extends BaseTest{
	
	@BeforeClass
	public static void login() {
	Map<String, String>	login = new HashMap<String, String>();
    login.put("email", "testeapi@api.com.br");
    login.put("senha", "123456");
    
    String TOKEN = given()
    		   .body(login)
    		.when()
    		   .post("/signin")
    		.then()
    		   .statusCode(200)
    		   .extract().path("token");
    		   
    		   RestAssured.requestSpecification.header("Authorization", "JWT " + TOKEN);
    		   RestAssured.get("/reset").then().statusCode(200);
 
	}

}
