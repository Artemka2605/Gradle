package autotests.tests.DuckActionController;

import autotests.clients.DuckActionsClient;
import autotests.payloads.DuckCreatePayload;
import autotests.payloads.DuckWingsState;
import autotests.payloads.MessageStringPayload;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.context.TestContext;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

import static com.consol.citrus.DefaultTestActionBuilder.action;
import static com.consol.citrus.container.FinallySequence.Builder.doFinally;

public class SwimTest extends DuckActionsClient {

    // ошибка 404 (лапки не найдены), хотя уточка существует в бд. ОР: 200 OK
    @Test(description = "Проверка, что уточка, существующая в бд (id), может плавать")
    @CitrusTest
    public void DuckSwimWithExistingID(@Optional @CitrusResource TestCaseRunner runner) {
        DuckCreatePayload duck = new DuckCreatePayload();
        duck.color("string").height(0.15).material("wood").sound("quack").wingsState(DuckWingsState.FIXED);
        createDuck(runner, duck);
        extractIdFromResponse(runner);
        duckTryToSwim(runner, "${duckId}");

        validateResponseFromPayload(runner, new MessageStringPayload().message("{\n" +
                "\"message\": \"I’m swimming\"\n" +
                "}"));

        doFinally().actions(
                runner.$(
                        action(ctx -> deleteDuck(runner)))
        );
    }

    // Ошибка 404. ОР: 200 OK
    @Test(description = "Проверка, что уточка, несуществующая в бд (нет такого id), не будет плавать")    @CitrusTest
    public void DuckSwimWithInvalidID(@Optional @CitrusResource TestCaseRunner runner, @CitrusResource TestContext context) {
        DuckCreatePayload duck = new DuckCreatePayload();
        duck.color("string").height(0.15).material("wood").sound("quack").wingsState(DuckWingsState.FIXED);
        createDuck(runner, duck);
        extractIdFromResponse(runner);

        // +1 для взятия несуществующего id в БД.
        int invalidId = Integer.parseInt(context.getVariable("${duckId}")) + 1;
        duckTryToSwim(runner, String.valueOf(invalidId));
        validateResponseFromString(runner, "{\n" +
                "\"message\": \"I’m swimming\"\n" +
                "}");

        doFinally().actions(
                runner.$(
                        action(ctx -> deleteDuck(runner)))
        );
    }

}
