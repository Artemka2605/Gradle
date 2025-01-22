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

import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import static com.consol.citrus.dsl.MessageSupport.MessageBodySupport.fromBody;


public class DuckActionQuackTest extends TestNGCitrusSpringSupport {
    @Test(description = "Проверка, что уточка с корректным нечётным id и корректным звуком (quack) будет крякать")
    @CitrusTest
    public void DuckQuackWithOddIdAndCorrectSound(@Optional @CitrusResource TestCaseRunner runner) {
        int duckId, repetitionCount = 1, soundCount = 1;
        double height = 0.15;
        String color = "string", material = "wood",
                sound = "quack", wingsState = "ACTIVE";
        //  quack является корректным звуком. Остальные дают звук moo
        do {
            // Не могу извлечь id из тела ответа и присвоить его переменной.
            // получается вечный цикл создания уточки
            createDuck(runner, color, height, material, sound, wingsState);
            duckId = extractIdFromResponse(runner);
            System.out.println("Extracted duckId: " + duckId);

        } while (duckId % 2 != 1);

        duckQuack(runner, duckId, repetitionCount, soundCount);
        validateResponse(runner, "{\n" +
                "\"sound\": \"quack\"\n" +
                "}");
        // проверяем одно повторение кряка и кол-во звуков, т.к. это не важно для этого теста.
    }


    @Test(description = "Проверка, что уточка с корректным чётным id и корректным звуком (quack) будет крякать")
    @CitrusTest
    public void DuckQuackWithEvenIdAndCorrectSound(@Optional @CitrusResource TestCaseRunner runner) {
        int duckId, repetitionCount = 1, soundCount = 1;
        double height = 0.15;
        String color = "string", material = "wood",
                sound = "quack", wingsState = "ACTIVE";
        //  quack является корректным звуком. Остальные дают звук moo
        do {
            // Не могу извлечь id из тела ответа и присвоить его переменной.
            // получается вечный цикл создания уточки
            createDuck(runner, color, height, material, sound, wingsState);
            duckId = extractIdFromResponse(runner);
            System.out.println("Extracted duckId: " + duckId);

        } while (duckId % 2 != 0);

        duckQuack(runner, duckId, repetitionCount, soundCount);
        validateResponse(runner, "{\n" +
                "\"sound\": \"quack\"\n" +
                "}");
        // проверяем одно повторение кряка и кол-во звуков, т.к. это не важно для этого теста.
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


    public void duckQuack(TestCaseRunner runner, int id, int repetitionCount, int soundCount) {
        runner.$(http()
                .client("http://localhost:2222")
                .send()
                .get("/api/duck/action/quack")
                .queryParam("id", String.valueOf(id))
                .queryParam("repetitionCount", String.valueOf(repetitionCount))
                .queryParam("soundCount", String.valueOf(soundCount))
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
