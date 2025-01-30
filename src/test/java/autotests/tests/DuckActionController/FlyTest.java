package autotests.tests.DuckActionController;

import autotests.clients.DuckActionsClient;
import autotests.payloads.DuckCreate;
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

public class FlyTest extends DuckActionsClient {
    @Test(description = "Проверка, что уточка с активными крыльями может летать")
    @CitrusTest
    public void DuckFlyWithActiveWings(@Optional @CitrusResource TestCaseRunner runner, @CitrusResource TestContext context) {
        DuckCreate duck = createDuckObject();
        duck.wingsState(DuckWingsState.ACTIVE);
        createDuck(runner, duck);

        extractIdFromResponse(runner);
        duckFly(runner, "${duckId}");//  context.getVariable("${duckId}")
        validateResponseFromResources(runner, "DuckActionsTest/successfulFly.json");

        doFinally().actions(
                runner.$(
                        action(ctx -> deleteDuck(runner, context.getVariable("${duckId}")))
                ));
    }

    @Test(description = "Проверка, что уточка со связанными крыльями НЕ может летать")
    @CitrusTest
    public void DuckFlyWithFixedWings(@Optional @CitrusResource TestCaseRunner runner, @CitrusResource TestContext context) {
        DuckCreate duck = createDuckObject();
        duck.wingsState(DuckWingsState.FIXED);
        createDuck(runner, duck);

        extractIdFromResponse(runner);
        duckFly(runner, "${duckId}");

        MessageStringPayload payloadMessage = new MessageStringPayload();
        payloadMessage.message("I can not fly :C");
        validateResponseFromPayload(runner, payloadMessage);

        doFinally().actions(
                runner.$(
                        action(ctx -> deleteDuck(runner, context.getVariable("${duckId}")))
                ));
    }

    @Test(description = "Проверка, что уточка с неопределёнными крыльями может летать")
    @CitrusTest
    public void DuckFlyWithUndefinedWings(@Optional @CitrusResource TestCaseRunner runner, @CitrusResource TestContext context) {
        DuckCreate duck = createDuckObject();
        duck.wingsState(DuckWingsState.UNDEFINED);
        createDuck(runner, duck);

        extractIdFromResponse(runner);
        duckFly(runner, "${duckId}");
        validateResponseFromString(runner, "{\n" +
                "\"message\": \"Wings are not detected :(\"\n" +
                "}");

        doFinally().actions(
                runner.$(
                        action(ctx -> deleteDuck(runner, context.getVariable("${duckId}")))
                ));
    }
}
