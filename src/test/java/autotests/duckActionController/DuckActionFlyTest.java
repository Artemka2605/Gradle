package autotests.duckActionController;

import autotests.duckController.DuckDeleteTest;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.context.TestContext;
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

public class DuckActionFlyTest extends TestNGCitrusSpringSupport {

    @Test(description = "Проверка, что уточка с активными крыльями может летать")
    @CitrusTest
    public void DuckFlyWithActiveWings(@Optional @CitrusResource TestCaseRunner runner, @CitrusResource TestContext context) {
        // создаём уточку и задаём переменные в контексте
        runner.variable("color", "string");
        runner.variable("height", 0.15);
        runner.variable("material", "wood");
        runner.variable("sound", "quack");
        runner.variable("wingsState", "ACTIVE"); //
        createDuck(runner, context);

        extractIdFromResponse(runner);
        duckFly(runner, context.getVariable("${duckId}"));
        validateResponse(runner, "{\n" +
                "\"message\": \"I am flying :)\"\n" +
                "}");

        // удаление созданной утки
        DuckDeleteTest deleteTest = new DuckDeleteTest();
        doFinally().actions(
                runner.$(
                        action(ctx ->
                                deleteTest.deleteDuck(runner, context.getVariable("${duckId}")))
                ));
    }

    @Test(description = "Проверка, что уточка со связанными крыльями НЕ может летать")
    @CitrusTest
    public void DuckFlyWithFixedWings(@Optional @CitrusResource TestCaseRunner runner, @CitrusResource TestContext context) {
        // создаём уточку и задаём переменные в контексте
        runner.variable("color", "string");
        runner.variable("height", 0.15);
        runner.variable("material", "wood");
        runner.variable("sound", "quack");
        runner.variable("wingsState", "FIXED"); //
        createDuck(runner, context);

        extractIdFromResponse(runner);
        duckFly(runner, context.getVariable("${duckId}"));
        validateResponse(runner, "{\n" +
                "\"message\": \"I can not fly :C\"\n" +
                "}");

        // удаление созданной утки
        DuckDeleteTest deleteTest = new DuckDeleteTest();
        doFinally().actions(
                runner.$(
                        action(ctx ->
                                deleteTest.deleteDuck(runner, context.getVariable("${duckId}")))
                ));
    }

    @Test(description = "Проверка, что уточка с неопределёнными крыльями может летать")
    @CitrusTest
    public void DuckFlyWithUndefinedWings(@Optional @CitrusResource TestCaseRunner runner, @CitrusResource TestContext context) {
        // создаём уточку и задаём переменные в контексте
        runner.variable("color", "string");
        runner.variable("height", 0.15);
        runner.variable("material", "wood");
        runner.variable("sound", "quack");
        runner.variable("wingsState", "UNDEFINED"); //
        createDuck(runner, context);

        extractIdFromResponse(runner);
        duckFly(runner, context.getVariable("${duckId}"));
        validateResponse(runner, "{\n" +
                "\"message\": \"Wings are not detected :(\"\n" +
                "}");
        // удаление созданной утки
        DuckDeleteTest deleteTest = new DuckDeleteTest();
        doFinally().actions(
                runner.$(
                        action(ctx ->
                                deleteTest.deleteDuck(runner, context.getVariable("${duckId}")))
                ));
    }


    public void createDuck(@CitrusResource TestCaseRunner runner, @CitrusResource TestContext context) {
        runner.$(
                http()
                        .client("http://localhost:2222")
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

    public void duckFly(TestCaseRunner runner, String id) {
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

    public void extractIdFromResponse(@CitrusResource TestCaseRunner runner) {
        runner.$(
                http()
                        .client("http://localhost:2222")
                        .receive()
                        .response(HttpStatus.OK)
                        .message()
                        .type(MessageType.JSON)
                        .extract(fromBody().expression("$.id", "duckId"))

        );
    }
}







