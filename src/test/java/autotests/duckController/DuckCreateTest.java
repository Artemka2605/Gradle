package autotests.duckController;

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

public class DuckCreateTest extends TestNGCitrusSpringSupport {
    @Test(description = "Проверка, что создаётся уточка с материалом rubber")
    @CitrusTest
    public void DuckCreateWithRubberMaterial(@Optional @CitrusResource TestCaseRunner runner){
        int duckId;
        double height = 0.15;
        String color = "string", material = "rubber",
                sound = "string", wingsState = "ACTIVE";

        createDuck(runner, color, height, material, sound, wingsState);
        duckId = extractIdFromResponse(runner);
        validateResponse(runner, "{\n" +
                " \"color\": \"" + color + "\",\n" +
                " \"height\": " + height + ",\n" +
                " \"id\": " + duckId + ",\n" +
                " \"material\": \"" + material + "\",\n" +
                " \"sound\": \"" + sound + "\",\n" +
                " \"wingsState\": \"" + wingsState + "\"\n" + "} ");

        //удаление созданной утки
        DuckDeleteTest deleteTest = new DuckDeleteTest();
        doFinally()
                .actions(
                        action(context -> deleteTest
                                .tryToDeleteDuck(runner, duckId))
                );
    }

    @Test(description = "Проверка, что создаётся уточка с материалом wood")
    @CitrusTest
    public void DuckCreateWithWoodMaterial(@Optional @CitrusResource TestCaseRunner runner){
        int duckId;
        double height = 0.15;
        String color = "string", material = "wood",
                sound = "string", wingsState = "ACTIVE";

        createDuck(runner, color, height, material, sound, wingsState);
        duckId = extractIdFromResponse(runner);
        validateResponse(runner, "{\n" +
                " \"color\": \"" + color + "\",\n" +
                " \"height\": " + height + ",\n" +
                " \"id\": " + duckId + ",\n" +
                " \"material\": \"" + material + "\",\n" +
                " \"sound\": \"" + sound + "\",\n" +
                " \"wingsState\": \"" + wingsState + "\"\n" + "} ");

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
        //runner.getVariable();
        return runner.variable("duckId", duckId);
    }
}
