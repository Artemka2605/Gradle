package autotests.tests.DuckActionController;

import autotests.clients.DuckActionsClient;
import autotests.payloads.MessageStringPayload;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

import static com.consol.citrus.container.FinallySequence.Builder.doFinally;

@Epic("Тесты на duck-action-controller")
@Feature("Эндпоинт /api/duck/action/fly")
public class FlyTest extends DuckActionsClient {
    @Test(description = "Проверка, что уточка с активными крыльями может летать")
    @CitrusTest
    public void DuckFlyWithActiveWings(@Optional @CitrusResource TestCaseRunner runner) {
        String id = getUniqueId(runner);
        runner.variable("duckId", id);
        runner.$(doFinally().actions(ctx -> dbQuery(runner,
                "DELETE FROM DUCK WHERE ID=${duckId}")));
        // создание утки
        dbQuery(runner, "INSERT INTO DUCK (ID, COLOR, HEIGHT, MATERIAL, SOUND, WINGS_STATE)\n" +
                "VALUES (${duckId}, 'string', 0.15, 'wood', 'quack', 'ACTIVE');");
        duckFly(runner, "${duckId}");
        validateResponseFromResources(runner, "DuckActionsTest/successfulFly.json");
    }

    @Test(description = "Проверка, что уточка со связанными крыльями НЕ может летать")
    @CitrusTest
    public void DuckFlyWithFixedWings(@Optional @CitrusResource TestCaseRunner runner) {
        String id = getUniqueId(runner);
        runner.variable("duckId", id);
        runner.$(doFinally().actions(ctx -> dbQuery(runner,
                "DELETE FROM DUCK WHERE ID=${duckId}")));
        // создание утки
        dbQuery(runner, "INSERT INTO DUCK (ID, COLOR, HEIGHT, MATERIAL, SOUND, WINGS_STATE)\n" +
                "VALUES (${duckId}, 'string', 0.15, 'wood', 'quack', 'FIXED');");
        duckFly(runner, "${duckId}");

        MessageStringPayload payloadMessage = new MessageStringPayload();
        payloadMessage.message("I can’t fly");
        validateResponseFromPayload(runner, payloadMessage);
    }

    @Test(description = "Проверка, что уточка с неопределёнными крыльями может летать")
    @CitrusTest
    public void DuckFlyWithUndefinedWings(@Optional @CitrusResource TestCaseRunner runner) {
        String id = getUniqueId(runner);
        runner.variable("duckId", id);
        runner.$(doFinally().actions(ctx -> dbQuery(runner,
                "DELETE FROM DUCK WHERE ID=${duckId}")));
        // создание утки
        dbQuery(runner, "INSERT INTO DUCK (ID, COLOR, HEIGHT, MATERIAL, SOUND, WINGS_STATE)\n" +
                "VALUES (${duckId}, 'string', 0.15, 'wood', 'quack', 'UNDEFINED');");
        duckFly(runner, "${duckId}");
        validateResponseFromString(runner, "{\n" +
                "\"message\": \"Wings are not detected :(\"\n" +
                "}");
    }
}
