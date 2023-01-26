package helloname;

import com.google.gson.Gson;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {
    /**
     * This function listens at endpoint "/api/HttpExample". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/HttpExample
     * 2. curl "{your host}/api/HttpExample?name=HTTP%20Query"
     */
    @FunctionName("HttpExample")
    public HttpResponseMessage run(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.POST},
                authLevel = AuthorizationLevel.ANONYMOUS)
                HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        String body = getBodyFromRequest(request);
        
        ResponseDto responseDto = jsonToResponseDto(body);
        
        responseDto.setConteudo(responseDto.getConteudo() + " | " + LocalDateTime.now());
        
        return buildResponse(request, responseDto);
    }
    
    private HttpResponseMessage buildResponse(HttpRequestMessage<Optional<String>> request, ResponseDto responseDto) {
    	// Parse query parameter
    	String strRetorno = responseDtoToJson(responseDto);
        if (responseDto == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).header("Content-Type", "application/json").body("{}").build();
        } else {
            return request.createResponseBuilder(HttpStatus.OK).header("Content-Type", "application/json").body(strRetorno).build();
        }
    }
    
    public String getBodyFromRequest(HttpRequestMessage<Optional<String>> request){
    	final String query = request.getQueryParameters().get("");
        final String body = request.getBody().orElse(query);
        return body;
    }
    
    private ResponseDto jsonToResponseDto(String strJson) {
    	Gson g = new Gson();
    	return g.fromJson(strJson, ResponseDto.class);
    }
    
    private String responseDtoToJson(ResponseDto responseDto) {
    	Gson g = new Gson();
    	return g.toJson(responseDto);
    }
    
    private class ResponseDto{
    	private String conteudo;

		public String getConteudo() {
			return conteudo;
		}

		public void setConteudo(String conteudo) {
			this.conteudo = conteudo;
		}
    }
}
