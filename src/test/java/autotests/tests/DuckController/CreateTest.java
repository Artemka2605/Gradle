package autotests.tests.DuckController;

import autotests.clients.DuckActionsClient;
import autotests.payloads.DuckCreate;
import autotests.payloads.DuckCreatePayload;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.context.TestContext;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

import static com.consol.citrus.DefaultTestActionBuilder.action;
import static com.consol.citrus.container.FinallySequence.Builder.doFinally;

public class CreateTest extends DuckActionsClient {

    @Test(description = "Проверка, что создаётся уточка с материалом rubber")
    @CitrusTest
    public void DuckCreateWithRubberMaterial(@Optional @CitrusResource TestCaseRunner runner, @CitrusResource TestContext context) {
        DuckCreate duck = createDuckObject();
        duck.material("rubber");
        createDuck(runner, duck);
        extractIdFromResponse(runner);

        DuckCreatePayload payload = new DuckCreatePayload()
                .id("${duckId}");
        //todo: Action timeout after 5000 milliseconds. Failed to receive message on endpoint: 'duckService'
        validateResponseFromPayload(runner, payload);

        doFinally().actions(
                runner.$(
                        action(ctx -> deleteDuck(runner, context.getVariable("${duckId}")))
                ));
    }

    @Test(description = "Проверка, что создаётся уточка с материалом wood")
    @CitrusTest
    public void DuckCreateWithWoodMaterial(@Optional @CitrusResource TestCaseRunner runner, @CitrusResource TestContext context) {
        DuckCreate duck = createDuckObject();
        duck.material("wood");
        createDuck(runner, duck);
        extractIdFromResponse(runner);

        DuckCreatePayload payload = new DuckCreatePayload()
                .id("${duckId}");

        //todo: Action timeout after 5000 milliseconds. Failed to receive message on endpoint: 'duckService'
        validateResponseFromPayload(runner, payload);

        doFinally().actions(
                runner.$(
                        action(ctx -> deleteDuck(runner, context.getVariable("${duckId}")))
                ));
    }

}
