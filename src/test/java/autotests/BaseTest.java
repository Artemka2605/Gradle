package autotests;

import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.message.builder.ObjectMappingPayloadBuilder;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Step;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.test.context.ContextConfiguration;

import static com.consol.citrus.actions.ExecuteSQLAction.Builder.sql;
import static com.consol.citrus.actions.ExecuteSQLQueryAction.Builder.query;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;


@ContextConfiguration(classes = {EndpointConfig.class})
public class BaseTest extends TestNGCitrusSpringSupport {

    @Autowired
    protected HttpClient duckService;

    @Autowired
    protected SingleConnectionDataSource dataBaseConnection;

    public void dbQuery(@CitrusResource TestCaseRunner runner, String query) {
        runner.$(sql(dataBaseConnection)
                .statement(query));
    }

    public String getUniqueId(@CitrusResource TestCaseRunner runner) {
        String uniqueId;
        boolean isUnique = false;
        int count = 0;
        do {
            uniqueId = String.valueOf((int) (Math.random() * 10000));

            runner.$(query(dataBaseConnection)
                    .statement("SELECT COUNT(*) AS count FROM DUCK WHERE ID = "+uniqueId));
            count = runner.variable("count", count);
            if (count == 0){
                isUnique = true;
            }
        } while (!isUnique);

        return uniqueId;
    }

    @Step("Эндпоинт для удаления уточки")
    public void deleteDuck(TestCaseRunner runner) {
        runner.$(http()
                .client(duckService)
                .send()
                .delete("/api/duck/delete")
                .queryParam("id", "${duckId}")
        );
    }

    @Step("Эндпоинт для обновления параметров уточки")
    public void updateDuckColorAndHeight(@CitrusResource TestCaseRunner runner, String color, Double height, String material, String sound) {
        runner.$(http()
                .client(duckService)
                .send()
                .put("/api/duck/update")
                .queryParam("color", color)
                .queryParam("height", String.valueOf(height))
                .queryParam("id", "${duckId}")
                .queryParam("material", material)
                .queryParam("sound", sound)
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
}
