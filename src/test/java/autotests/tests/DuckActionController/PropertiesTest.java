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

public class PropertiesTest extends DuckActionsClient {


    // в ответе приходит пустой ответ (пустой json) и валидация ответа проваливается вместе с тестом.
    @Test(description = "Проверка, что приходит ответ с характеристиками уточки (кроме id) с чётным ID и материалом wood")
    @CitrusTest
    public void DuckPropertiesWithEvenId(@Optional @CitrusResource TestCaseRunner runner, @CitrusResource TestContext context ) {
        DuckCreate duck = createDuckObject();
        duck.material("wood");
        do {
            createDuck(runner, duck);
            extractIdFromResponse(runner);
        } while (Integer.parseInt(context.getVariable("${duckId}")) % 2 != 0);

        showDuckProperties(runner, "${duckId}");
        // в ответе приходит пустой ответ (пустой json) и валидация ответа проваливается
        validateResponseFromPayload(runner, duck);

        doFinally().actions(
                runner.$(
                        action(ctx -> deleteDuck(runner, context.getVariable("${duckId}")))
                ));
    }

    // в ответе приходит пустой ответ (пустой json) и валидация ответа проваливается вместе с тестом.
    @Test(description = "Проверка, что приходит ответ с характеристиками уточки (кроме id) с нечётным ID и материалом rubber")
    @CitrusTest
    public void DuckPropertiesWithOddId(@Optional @CitrusResource TestCaseRunner runner, @CitrusResource TestContext context) {
        DuckCreate duck = createDuckObject();
        duck.material("rubber");
        do {
            createDuck(runner, duck);
            extractIdFromResponse(runner);
        } while (Integer.parseInt(context.getVariable("${duckId}")) % 2 != 1);

        showDuckProperties(runner, "${duckId}");
        // в ответе приходит пустой ответ (пустой json) и валидация ответа проваливается
        validateResponseFromPayload(runner, duck);

        doFinally().actions(
                runner.$(
                        action(ctx -> deleteDuck(runner, context.getVariable("${duckId}")))
                ));
    }

}
