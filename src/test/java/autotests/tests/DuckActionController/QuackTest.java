package autotests.tests.DuckActionController;

import autotests.clients.DuckActionsClient;
import autotests.payloads.DuckCreate;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.context.TestContext;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

import static com.consol.citrus.DefaultTestActionBuilder.action;
import static com.consol.citrus.container.FinallySequence.Builder.doFinally;

public class QuackTest extends DuckActionsClient {

    @Test(description = "Проверка, что уточка с корректным нечётным id и корректным звуком (quack) будет крякать")
    @CitrusTest
    public void DuckQuackWithOddIdAndCorrectSound(@Optional @CitrusResource TestCaseRunner runner, @CitrusResource TestContext context) {
        DuckCreate duck = createDuckObject();
        duck.sound("quack");
        runner.variable("repetitionCount", 1);
        runner.variable("soundCount", 1);

        do {
            createDuck(runner, duck);
            extractIdFromResponse(runner);
        } while (Integer.parseInt(context.getVariable("${duckId}")) % 2 != 1);

        duckQuack(runner, "${duckId}", "${repetitionCount}", "${soundCount}");
        validateResponseFromString(runner, "{\n" +
                "\"sound\": \"quack\"\n" +
                "}");

        doFinally().actions(
                runner.$(
                        action(ctx -> deleteDuck(runner, context.getVariable("${duckId}")))
                ));
    }

    // Значение "звук" в ответе на duckQuack равно moo, а ожидается quack (ТЕСТ ПРОВАЛЕН)
    @Test(description = "Проверка, что уточка с корректным чётным id и корректным звуком (quack) будет крякать")
    @CitrusTest
    public void DuckQuackWithEvenIdAndCorrectSound(@Optional @CitrusResource TestCaseRunner runner, @CitrusResource TestContext context) {
        DuckCreate duck = createDuckObject();
        duck.sound("quack");
        runner.variable("repetitionCount", 1);
        runner.variable("soundCount", 1);

        do {
            createDuck(runner, duck);
            extractIdFromResponse(runner);
        } while (Integer.parseInt(context.getVariable("${duckId}")) % 2 != 0);

        duckQuack(runner, "${duckId}", "${repetitionCount}", "${soundCount}");
        validateResponseFromResources(runner, "DuckActionsTest/successfulQuack.json");

        doFinally().actions(
                runner.$(
                        action(ctx -> deleteDuck(runner, context.getVariable("${duckId}")))
                ));
    }

}
