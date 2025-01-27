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


public class DuckUpdateTest extends TestNGCitrusSpringSupport {
    int duckId;
    double height = 0.15;
    String color = "string", material = "rubber",
            sound = "string", wingsState = "ACTIVE";

    @Test(description = "Проверка, что у уточки изменяются свойства: 'цвет' и 'высота' ")
    @CitrusTest
    public void DuckUpdateColorAndHeight(@Optional @CitrusResource TestCaseRunner runner) {
        // Новые параметры уточки:
        color = "blue";
        height = 0.05;

        createDuck(runner, color, height, material, sound, wingsState);
        duckId = extractIdFromResponse(runner);
        updateDuckColorAndHeight(runner, duckId, color, height, material, sound);
        validateResponse(runner, "{\n" +
                "\"message\": \"Duck with id = " + duckId + "is updated\"\n" +
                "}");

        //удаление созданной утки
//        DuckDeleteTest deleteTest = new DuckDeleteTest();
//        doFinally()
//                .actions(
//                        action(context -> deleteTest
//                                .tryToDeleteDuck(runner, duckId))
//                );
    }


    @Test(description = "Проверка, что у уточки изменяются свойства: 'цвет' и 'звук' ")
    @CitrusTest
    public void DuckUpdateColorAndSound(@Optional @CitrusResource TestCaseRunner runner) {
        // Новые параметры уточки:
        color = "red";
        sound = "quack";

        createDuck(runner, color, height, material, sound, wingsState);
        duckId = extractIdFromResponse(runner);
        updateDuckColorAndHeight(runner, duckId, color, height, material, sound);
        validateResponse(runner, "{\n" +
                "\"message\": \"Duck with id = " + duckId + "is updated\"\n" +
                "}");

        //удаление созданной утки
//        DuckDeleteTest deleteTest = new DuckDeleteTest();
//        doFinally()
//                .actions(
//                        action(context -> deleteTest
//                                .tryToDeleteDuck(runner, duckId))
//                );
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

    public void updateDuckColorAndHeight(TestCaseRunner runner, int id, String color, double height,
                                         String material, String sound) {
        runner.$(http()
                .client("http://localhost:2222")
                .send()
                .put("/api/duck/update")
                .queryParam("color", color)
                .queryParam("height", String.valueOf(height))
                .queryParam("id", String.valueOf(id))
                .queryParam("material", material)
                .queryParam("sound", sound)
                // нужно ли передавать необязательный параметр wingsState ?
                // на тест он не должен влиять.
                // Как сделать обновление некоторых параметров уточки без передачи всех остальных ?
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
