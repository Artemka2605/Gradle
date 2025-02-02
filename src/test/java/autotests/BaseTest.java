package autotests;

import autotests.payloads.DuckPropertiesPayload;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.http.client.HttpClient;
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
import static com.consol.citrus.actions.ExecuteSQLQueryAction.Builder.query;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;


@ContextConfiguration(classes = {EndpointConfig.class})
public class BaseTest extends TestNGCitrusSpringSupport {

    @Autowired
    protected HttpClient duckService;

    @Autowired
    protected SingleConnectionDataSource dataBaseConnection;

    // используем модификатор доступа для общих методов protected
    protected void sendGetRequest(@CitrusResource TestCaseRunner runner, HttpClient url, String path) {
        runner.$(http()
                .client(url)
                .send()
                .get(path)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        );
    }

    protected void sendPostRequest(@CitrusResource TestCaseRunner runner, HttpClient url, String path,
                                   DuckPropertiesPayload duckCreateBodyFromPayload) {
        runner.$(http()
                .client(url)
                .send()
                .post(path)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new ObjectMappingPayloadBuilder(duckCreateBodyFromPayload, new ObjectMapper()))
        );
    }
    protected void sendPutRequest(@CitrusResource TestCaseRunner runner, HttpClient url, String path) {
        runner.$(http()
                .client(url)
                .send()
                .put(path)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        );
    }

    protected void sendDeleteRequest(@CitrusResource TestCaseRunner runner, HttpClient url, String path) {
        runner.$(http()
                .client(url)
                .send()
                .delete(path)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        );
    }

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
    // В expectedPayload должен приходить экземпляр модели с определёнными полями, иначе int значения будут 0 и ожидаться в результате,
    //  а string будут null и они не будут являться ожидаемыми параметрами.
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
