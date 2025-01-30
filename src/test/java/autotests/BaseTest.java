package autotests;

import autotests.payloads.DuckCreatePayload;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.message.MessageType;
import com.consol.citrus.message.builder.ObjectMappingPayloadBuilder;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.test.context.ContextConfiguration;

import static com.consol.citrus.actions.ExecuteSQLAction.Builder.sql;
import static com.consol.citrus.dsl.MessageSupport.MessageBodySupport.fromBody;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;


@ContextConfiguration(classes = {EndpointConfig.class})
public class BaseTest extends TestNGCitrusSpringSupport {

    @Autowired
    protected HttpClient duckService;

    @Autowired
    protected SingleConnectionDataSource dataBaseConnection;

    public void dbCreateDuck(@CitrusResource TestCaseRunner runner, DuckCreate duckCreateBody) {
        runner.$(sql(dataBaseConnection)
                .statement("SELECT * FROM DUCK WHERE ... =" + duckCreateBody.color() + "'")
        );
    }

    public void createDuck(@CitrusResource TestCaseRunner runner, Object duckCreateBody) {
        runner.$(
                http()
                        .client(duckService)
                        .send()
                        .post("/api/duck/create")
                        .message()
                        .contentType("application/json")
                        .body(new ObjectMappingPayloadBuilder(duckCreateBody, new ObjectMapper()))
        );
    }

    public void deleteDuck(TestCaseRunner runner) {
        runner.$(http()
                .client(duckService)
                .send()
                .delete("/api/duck/delete")
                .queryParam("id", "${duckId}")
        );
    }

    public void updateDuckColorAndHeight(@CitrusResource TestCaseRunner runner, String duckId, DuckCreatePayload duck) {
        runner.$(http()
                .client(duckService)
                .send()
                .put("/api/duck/update")
                .queryParam("color", duck.color())
                .queryParam("height", String.valueOf(duck.height()))
                .queryParam("id", duckId)
                .queryParam("material",duck.material())
                .queryParam("sound", duck.sound())
        );
    }


    public void validateResponseFromString(@CitrusResource TestCaseRunner runner, String responseMessage) {
        runner.$(http()
                .client(duckService)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(responseMessage)
        );
    }

    public void validateResponseFromResources(@CitrusResource TestCaseRunner runner, String expectedPayload) {
        runner.$(http()
                .client(duckService)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new ClassPathResource(expectedPayload))
        );
    }

    public void validateResponseFromPayload(@CitrusResource TestCaseRunner runner, Object expectedPayload) {
        runner.$(http()
                .client(duckService)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new ObjectMappingPayloadBuilder(expectedPayload, new ObjectMapper()))
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
