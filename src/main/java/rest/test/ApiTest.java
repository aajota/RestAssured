package rest.test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
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
	@Test
	public void deveInserirMovimentacaoComSucesso() {
		Movimentacao mov = getMovimentacaoValida();
		
		 given()
		    .header("Authorization", "JWT " + TOKEN)
		    .body(mov)
		.when()
		   .post("/transacoes")
		.then()
		   .statusCode(201)
		   ;
	}
	@Test
	public void deveInserirDadossObrigatoriosMovimentacao() {
		Movimentacao mov = new Movimentacao();
		mov.setConta_id(1442012);
	//	mov.setUsuario_id(usuario_id);
		mov.setDescricao("Descricao da movimentacao");
		mov.setEnvolvido("Envolvido na mov");
		mov.setTipo("REC");
		mov.setData_transacao("10/05/2019");
		mov.setData_pagamento("10/10/2022");
		mov.setValor(100f);
		mov.setStatus(true);
		
		 given()
		    .header("Authorization", "JWT " + TOKEN)
		    .body(mov)
		.when()
		   .post("/transacoes")
		.then()
		   .statusCode(201)
		   ;
	}
	@Test
	public void deveValidarCamposObrigatoriosMovimentacao() {
		 given()
		    .header("Authorization", "JWT " + TOKEN)
		    .body("{}")
		.when()
		   .post("/transacoes")
		.then()
		   .statusCode(400)
		   .body("$", hasSize(8))
		   .body("msg", hasItems(
				   "Data da Movimentação é obrigatório",
				   "Data do pagamento é obrigatório",
				   "Descrição é obrigatório",
				   "Interessado é obrigatório",
				   "Valor é obrigatório",
				   "Valor deve ser um número",
				   "Conta é obrigatório",
				   "Situação é obrigatório"				   				   
				   ))
		   ;
	}
	
	@Test
	public void naoDeveInserirMovimentacaoComDataFutura() {
		Movimentacao mov = getMovimentacaoValida();
		mov.setData_transacao("17/10/2022");
		
		 given()
		    .header("Authorization", "JWT " + TOKEN)
		    .body(mov)
		.when()
		   .post("/transacoes")
		.then()
		   .statusCode(400)
		   .body("$", hasSize(1))
		   .body("msg", hasItem("Data da Movimentação deve ser menor ou igual à data atual"))
		   ;
	}
	
	@Test
	public void naoDeveRemoverContaComMovimentacao() {
		 given()
		    .header("Authorization", "JWT " + TOKEN)
		.when()
		   .delete("/contas/1442012")
		.then()
		   .statusCode(500)
		   .body("constraint", is("transacoes_conta_id_foreign"))
		  
		   ;
	}
	@Test
	public void deveCalcularSaldoContas() {
		 given()
		    .header("Authorization", "JWT " + TOKEN)
		.when()
		   .get("/saldo")
		.then()
		   .statusCode(200)
		   .body("find{it.conta_id == 1442012}.saldo", is("54845.00"))
		  
		   ;
	}
	@Test
	public void deveRemoverMovimentacao() {
		 given()
		    .header("Authorization", "JWT " + TOKEN)
		.when()
		   .delete("/transacoes/1346898")
		.then()
		   .statusCode(204)
		  
		   ;
	}
	
	private Movimentacao getMovimentacaoValida() {
		Movimentacao mov = new Movimentacao();
		mov.setConta_id(1442012);
	//	mov.setUsuario_id(usuario_id);
		mov.setDescricao("Descricao da movimentacao");
		mov.setEnvolvido("Envolvido na mov");
		mov.setTipo("REC");
		mov.setData_transacao("10/05/2019");
		mov.setData_pagamento("10/10/2022");
		mov.setValor(100f);
		mov.setStatus(true);
		return mov;
	}

}
