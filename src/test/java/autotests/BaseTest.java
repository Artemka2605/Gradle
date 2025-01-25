package autotests;

import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.message.MessageType;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;

import static com.consol.citrus.dsl.MessageSupport.MessageBodySupport.fromBody;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;


@ContextConfiguration(classes = {EndpointConfig.class})
public class BaseTest extends TestNGCitrusSpringSupport {

    @Autowired
    protected HttpClient duckService;

    public void setDuckVariablesInRunner(@CitrusResource TestCaseRunner runner){
        // устанавливает стандартные переменные, которые переопределяются в тестах, в контекст runner.
        runner.variable("color", "string");
        runner.variable("height", 0.15);
        runner.variable("material", "wood");
        runner.variable("sound", "quack");
        runner.variable("wingsState", "ACTIVE");
    }

    public void createDuck(@CitrusResource TestCaseRunner runner, @CitrusResource TestContext context) {
        runner.$(
                http()
                        .client(duckService)
                        .send()
                        .post("/api/duck/create")
                        .message()
                        .contentType("application/json")
                        .body(
                                "{\n" +
                                        " \"color\": \"" + context.getVariable("${color}") + "\",\n" +
                                        " \"height\": " + context.getVariable("${height}") + ",\n" +
                                        " \"material\": \"" + context.getVariable("${material}") + "\",\n" +
                                        " \"sound\": \"" + context.getVariable("${sound}") + "\",\n" +
                                        " \"wingsState\": \"" + context.getVariable("${wingsState}")+ "\"\n" + "} "
                        )
        );
    }

    public void deleteDuck(TestCaseRunner runner, String id) {
        runner.$(http()
                .client(duckService)
                .send()
                .delete("/api/duck/delete")
                .queryParam("id", id)
        );
    }

    public void updateDuckColorAndHeight(@CitrusResource TestCaseRunner runner,  @CitrusResource TestContext context) {
        runner.$(http()
                .client(duckService)
                .send()
                .put("/api/duck/update")
                .queryParam("color", context.getVariable("${color}"))
                .queryParam("height", context.getVariable("${height}"))
                .queryParam("id", context.getVariable("${duckId}"))
                .queryParam("material", context.getVariable("${material}"))
                .queryParam("sound", context.getVariable("${sound}"))

        );
    }


    public void validateResponse(@CitrusResource TestCaseRunner runner, String responseMessage) {
        runner.$(http()
                .client(duckService)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(responseMessage)
        );
    }

    public void extractIdFromResponse(@CitrusResource TestCaseRunner runner) {
        runner.$(
                http()
                        .client(duckService)
                        .receive()
                        .response(HttpStatus.OK)
                        .message()
                        .type(MessageType.JSON)
                        .extract(fromBody().expression("$.id", "duckId"))

        );
    }
}
