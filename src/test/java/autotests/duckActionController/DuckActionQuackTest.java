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

import static com.consol.citrus.actions.EchoAction.Builder.echo;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import static com.consol.citrus.dsl.MessageSupport.MessageBodySupport.fromBody;


public class DuckActionQuackTest extends TestNGCitrusSpringSupport {
    @Test(description = "Проверка, что показываются характеристиками уточки (кроме id) с чётным ID и материалом wood")
    @CitrusTest
    public void DuckQuackWithOddIdAndCorrectSound(@Optional @CitrusResource TestCaseRunner runner) {
        int duckId;
        double height = 0.15;
        String color = "string", material = "wood",
                sound = "quack", wingsState = "ACTIVE";
        do {
            // Не могу извлечь id из тела ответа и присвоить его переменной.
            // получается вечный цикл создания уточки
            createDuck(runner, color, height, material, sound, wingsState);
            duckId = extractIdFromResponse(runner);
            System.out.println("Extracted duckId: " + duckId);

        } while (duckId % 2 != 0);

        showDuckProperties(runner, String.valueOf(duckId));
        validateResponse(runner, "{\n" +
                " \"color\": \"" + color + "\",\n" +
                " \"height\": " + height + ",\n" +
                " \"material\": \"" + material + "\",\n" +
                " \"sound\": \"" + sound + "\",\n" +
                " \"wingsState\": \"" + wingsState + "\"\n" + "} ");
    }


    @Test(description = "Проверка, что приходит ответ с характеристиками уточки (кроме id) с нечётным ID и материалом")
    @CitrusTest
    public void DuckPropertiesWithOddId(@Optional @CitrusResource TestCaseRunner runner) {
        int duckId;
        double height = 0.15;
        String color = "string", material = "rubber",
                sound = "quack", wingsState = "ACTIVE";
        do {
            createDuck(runner, color, height, material, sound, wingsState);
            duckId = extractIdFromResponse(runner);
        } while (duckId % 2 != 1);

        showDuckProperties(runner, String.valueOf(duckId));
        validateResponse(runner, "{\n" +
                " \"color\": \"" + color + "\",\n" +
                " \"height\": " + height + ",\n" +
                " \"material\": \"" + material + "\",\n" +
                " \"sound\": \"" + sound + "\",\n" +
                " \"wingsState\": \"" + wingsState + "\"\n" + "} ");
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


    public void showDuckProperties(TestCaseRunner runner, String id) {
        runner.$(http()
                .client("http://localhost:2222")
                .send()
                .get("/api/duck/action/properties")
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
        //runner.getVariable();
        return runner.variable("duckId", duckId);
    }




















}
