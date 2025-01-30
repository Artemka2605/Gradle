package autotests.tests.DuckController;

import autotests.clients.DuckActionsClient;
import autotests.payloads.DuckCreatePayload;
import autotests.payloads.DuckWingsState;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

import static com.consol.citrus.DefaultTestActionBuilder.action;
import static com.consol.citrus.container.FinallySequence.Builder.doFinally;

public class UpdateTest extends DuckActionsClient {
    @Test(description = "Проверка, что у уточки изменяются свойства: 'цвет' и 'высота' ")
    @CitrusTest
    public void DuckUpdateColorAndHeight(@Optional @CitrusResource TestCaseRunner runner) {
        DuckCreatePayload duck = new DuckCreatePayload();
        duck.color("string").height(0.15).material("rubber").sound("quack").wingsState(DuckWingsState.FIXED);
        createDuck(runner, duck);
        extractIdFromResponse(runner);

        // todo: сделать сравнение значений цвета и высоты до изменения со значениями после
        duck.color("blue");
        duck.height(0.05);

        updateDuckColorAndHeight(runner, "${duckId}", duck);
        validateResponseFromString(runner, "{\n" +
                "\"message\": \"Duck with id = " + "${duckId}" + " is updated\"\n" +
                "}");

        doFinally().actions(
                runner.$(
                        action(ctx -> deleteDuck(runner)))
        );
    }


    @Test(description = "Проверка, что у уточки изменяются свойства: 'цвет' и 'звук' ")
    @CitrusTest
    public void DuckUpdateColorAndSound(@Optional @CitrusResource TestCaseRunner runner) {
        DuckCreatePayload duck = new DuckCreatePayload();
        duck.color("string").height(0.15).material("rubber").sound("quack").wingsState(DuckWingsState.FIXED);
        createDuck(runner, duck);
        extractIdFromResponse(runner);

        // todo: сделать сравнение значений цвета и высоты до изменения со значениями после
        duck.color("red");
        duck.sound("quack");

        updateDuckColorAndHeight(runner, "${duckId}", duck);
        validateResponseFromString(runner, "{\n" +
                "\"message\": \"Duck with id = " + "${duckId}" + " is updated\"\n" +
                "}");

        doFinally().actions(
                runner.$(
                        action(ctx -> deleteDuck(runner)))
        );
    }
}
