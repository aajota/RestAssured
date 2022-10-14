package rest.test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import core.BaseTest;

public class ApiTest extends BaseTest{
	
	private String TOKEN;
	
	@Before
	public void login() {
	Map<String, String>	login = new HashMap<String, String>();
    login.put("email", "testeapi@api.com.br");
    login.put("senha", "123456");
    
    TOKEN = given()
    		   .body(login)
    		.when()
    		   .post("/signin")
    		.then()
    		   .statusCode(200)
    		   .extract().path("token")
    		;
 
	}
	
	@Test
	public void naoDeveAcessarApiSemToken() {
		given()
		.when()
		   .get("/contas")
		.then()
		   .statusCode(401)
		;
	}
	
	@Test
	public void deveIncluirContaComSucesso() {
		 given()
		    .header("Authorization", "JWT " + TOKEN)
		    .body("{\"nome\": \"conta qualquer\"}")
		.when()
		   .post("/contas")
		.then()
		   .statusCode(201)
		;
		
	}
	@Test
	public void deveAlterarContaComSucesso() {
		 given()
		    .header("Authorization", "JWT " + TOKEN)
		    .body("{\"nome\": \"conta alterada\"}")
		.when()
		   .put("/contas/1442012")
		.then()
		   .statusCode(200)
		   .body("nome", is("conta alterada"))
		;
		
	}
	@Test
	public void naoDeveInserirContaComMesmoNome() {
		 given()
		    .header("Authorization", "JWT " + TOKEN)
		    .body("{\"nome\": \"conta alterada\"}")
		.when()
		   .post("/contas")
		.then()
		   .statusCode(400)
		   .body("error", is("Já existe uma conta com esse nome!"))
		   ;
	}
	
}
