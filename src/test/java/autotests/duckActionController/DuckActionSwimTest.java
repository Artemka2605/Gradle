package autotests.duckActionController;

import autotests.duckController.DuckDeleteTest;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.message.MessageType;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

import static com.consol.citrus.DefaultTestActionBuilder.action;
import static com.consol.citrus.container.FinallySequence.Builder.doFinally;
import static com.consol.citrus.dsl.MessageSupport.MessageBodySupport.fromBody;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;

public class DuckActionSwimTest extends TestNGCitrusSpringSupport {

    @Test(description = "Проверка, что уточка, существующая в бд (id), может плавать")
    @CitrusTest
    public void DuckSwimWithExistingID(@Optional @CitrusResource TestCaseRunner runner) {
        int duckId;
        double height = 0.15;
        String color = "string", material = "wood",
                sound = "quack", wingsState = "ACTIVE";

        createDuck(runner, color, height, material, sound, wingsState);
        duckId = extractIdFromResponse(runner);
        duckTryToSwim(runner, String.valueOf(duckId));
        validateResponse(runner, "{\n" +
                "\"message\": \"string\"\n" +
                "}");

        // какой ожидаемый текст сообщения должен быть, "string" или "уточка поплыла"?

        //удаление созданной утки
        DuckDeleteTest deleteTest = new DuckDeleteTest();
        doFinally()
                .actions(
                        action(context -> deleteTest
                                .tryToDeleteDuck(runner, duckId))
                );

    }


    @Test(description = "Проверка, что уточка, несуществующая в бд (нет такого id), не будет плавать")    @CitrusTest
    public void DuckSwimWithInvalidID(@Optional @CitrusResource TestCaseRunner runner) {

        int duckId;
        double height = 0.15;
        String color = "string", material = "wood",
                sound = "quack", wingsState = "ACTIVE";

        createDuck(runner, color, height, material, sound, wingsState);
        duckId = extractIdFromResponse(runner) + 1; // +1 для взятия несуществующего id в БД
        duckTryToSwim(runner, String.valueOf(duckId));
        validateResponse(runner, "{\n" +
                "\"message\": \"Paws are not found ((((\"\n" +
                "}");

        //удаление созданной утки
        DuckDeleteTest deleteTest = new DuckDeleteTest();
        doFinally()
                .actions(
                        action(context -> deleteTest
                                .tryToDeleteDuck(runner, duckId))
                );

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


    public void duckTryToSwim(TestCaseRunner runner, String id) {
        runner.$(http()
                .client("http://localhost:2222")
                .send()
                .get("/api/duck/action/swim")
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







