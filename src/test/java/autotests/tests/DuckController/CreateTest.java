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

public class CreateTest extends DuckActionsClient {

    @Test(description = "Проверка, что создаётся уточка с материалом rubber")
    @CitrusTest
    public void DuckCreateWithRubberMaterial(@Optional @CitrusResource TestCaseRunner runner) {
        DuckCreatePayload duck = new DuckCreatePayload();
        duck.color("string").height(0.15).material("rubber").sound("quack").wingsState(DuckWingsState.FIXED);
        createDuck(runner, duck);
        extractIdFromResponse(runner);
        String duck_id = runner.variable("duckId", "${duckId}");
        duck.id(Integer.parseInt(duck_id));

        //Ошибка: Action timeout after 5000 milliseconds. Failed to receive message on endpoint: 'duckService'
        validateResponseFromPayload(runner, duck);

        doFinally().actions(
                runner.$(
                        action(ctx -> deleteDuck(runner)))
        );
    }

    @Test(description = "Проверка, что создаётся уточка с материалом wood")
    @CitrusTest
    public void DuckCreateWithWoodMaterial(@Optional @CitrusResource TestCaseRunner runner) {
        DuckCreatePayload duck = new DuckCreatePayload();
        duck.color("string").height(0.15).material("wood").sound("quack").wingsState(DuckWingsState.FIXED);
        createDuck(runner, duck);
        extractIdFromResponse(runner);
        String duck_id = runner.variable("duckId", "${duckId}");
        duck.id(Integer.parseInt(duck_id));

        //Ошибка: Action timeout after 5000 milliseconds. Failed to receive message on endpoint: 'duckService'
        validateResponseFromPayload(runner, duck);

        doFinally().actions(
                runner.$(
                        action(ctx -> deleteDuck(runner)))
        );
    }

}
