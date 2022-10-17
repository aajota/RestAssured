package refac;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

import core.BaseTest;
import io.restassured.RestAssured;
import utils.ApiUtils;

public class ContasTest extends BaseTest{
	

	
	@Test
	public void deveIncluirContaComSucesso() {
		given()
		    .body("{\"nome\": \"Conta inserida\"}")
		.when()
		   .post("/contas")
		.then()
		   .statusCode(201)
		;
		
	}
	
	@Test
	public void deveAlterarContaComSucesso() {
		Integer CONTA_ID = ApiUtils.getIdContaPeloNome("Conta para alterar");
		 given()
		    .body("{\"nome\": \"Conta alterada\"}")
		    .pathParam("id", CONTA_ID)
		.when()
		   .put("/contas/{id}")
		.then()
		   .statusCode(200)
		   .body("nome", is("Conta alterada"))
		;
		
	}
	
	public Integer getIdContaPeloNome(String nome) {
		return RestAssured.get("/contas?nome="+nome).then().extract().path("id[0]");
	}
	
	@Test
	public void naoDeveInserirContaComMesmoNome() {
		 given()
		    .body("{\"nome\": \"Conta mesmo nome\"}")
		.when()
		   .post("/contas")
		.then()
		   .statusCode(400)
		   .body("error", is("Já existe uma conta com esse nome!"))
		   ;
	}

}
