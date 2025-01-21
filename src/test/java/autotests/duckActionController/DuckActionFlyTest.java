package autotests.duckActionController;

import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.message.MessageType;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

import static com.consol.citrus.dsl.MessageSupport.MessageBodySupport.fromBody;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;

public class DuckActionFlyTest extends TestNGCitrusSpringSupport {

    @Test(description = "Проверка, что уточка с активными крыльями может летать")
    @CitrusTest
    public void DuckFlyWithActiveWings(@Optional @CitrusResource TestCaseRunner runner) {
        int duckId;
        double height = 0.15;
        String color = "string", material = "wood",
                sound = "quack", wingsState = "ACTIVE";

        createDuck(runner, color, height, material, sound, wingsState);
        duckId = extractIdFromResponse(runner);
        duckTryToFly(runner, String.valueOf(duckId));
        validateResponse(runner, "{\n" +
                "\"message\": \"I am flying :)\"\n" +
                "}");

        //todo: в finaly сделать удаление созданной утки
    }


    @Test(description = "Проверка, что уточка со связанными крыльями НЕ может летать ")
    @CitrusTest
    public void DuckFlyWithFixedWings(@Optional @CitrusResource TestCaseRunner runner) {

        int duckId;
        double height = 0.15;
        String color = "string", material = "wood",
                sound = "quack", wingsState = "FIXED";

        createDuck(runner, color, height, material, sound, wingsState);
        duckId = extractIdFromResponse(runner);
        duckTryToFly(runner, String.valueOf(duckId));
        validateResponse(runner, "{\n" +
                "\"message\": \"I can not fly :C\"\n" +
                "}");

        //todo: в finaly сделать удаление созданной утки
    }
    @Test(description = "Проверка, что уточка с неопределённым значением крыльев выдаёт об этом сообщение")
    @CitrusTest
    public void DuckFlyWithUndefinedWings(@Optional @CitrusResource TestCaseRunner runner) {

        int duckId;
        double height = 0.15;
        String color = "string", material = "wood",
                sound = "quack", wingsState = "UNDEFINED";

        createDuck(runner, color, height, material, sound, wingsState);
        duckId = extractIdFromResponse(runner);
        duckTryToFly(runner, String.valueOf(duckId));
        validateResponse(runner, "{\n" +
                "\"message\": \"Wings are not detected :(\"\n" +
                "}");

        //todo: в finaly сделать удаление созданной утки
    }

    public void createDuck(TestCaseRunner runner, String color, double height, String material, String sound, String wingsState) {
        runner.$(
                http()
                        .client("http://localhost:2222")
                        .send()
                        .post("/api/duck/create")
                        .message()
                        .contentType("application/json")
                        .body(
                                "{\n" +
                                        " \"color\": \"" + color + "\",\n" +
                                        " \"height\": " + height + ",\n" +
                                        " \"material\": \"" + material + "\",\n" +
                                        " \"sound\": \"" + sound + "\",\n" +
                                        " \"wingsState\": \"" + wingsState + "\"\n" + "} "
                        )
        );
    }


    public void duckTryToFly(TestCaseRunner runner, String id) {
        runner.$(http()
                .client("http://localhost:2222")
                .send()
                .get("/api/duck/action/fly")
                .queryParam("id", id)
        );
    }

    public void validateResponse(TestCaseRunner runner, String responseMessage) {
        runner.$(http()
                .client("http://localhost:2222")
                .receive()
                .response(HttpStatus.OK)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(responseMessage)
        );
    }

    public int extractIdFromResponse(TestCaseRunner runner) {
        int duckId = -1;
        runner.$(
                http()
                        .client("http://localhost:2222")
                        .receive()
                        .response(HttpStatus.OK)
                        .message()
                        .type(MessageType.JSON)
                        .extract(fromBody().expression("$.id", "duckId"))

        );
        return runner.variable("duckId", duckId);
    }
}







