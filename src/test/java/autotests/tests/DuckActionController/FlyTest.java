package autotests.tests.DuckActionController;

import autotests.clients.DuckActionControllerClient;
import autotests.payloads.DuckPropertiesPayload;
import autotests.payloads.DuckWingsState;
import autotests.payloads.MessageStringPayload;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

import static com.consol.citrus.container.FinallySequence.Builder.doFinally;

@Epic("Тесты на duck-action-controller")
@Feature("Эндпоинт /api/duck/action/fly")
public class FlyTest extends DuckActionControllerClient {

    DuckPropertiesPayload duckProperties1 = new DuckPropertiesPayload()
            .color("string")
            .height(0.15)
            .material("wood")
            .sound("quack")
            .wingsState(DuckWingsState.ACTIVE);
    DuckPropertiesPayload duckProperties2 = new DuckPropertiesPayload()
            .color("blue")
            .height(0.5)
            .material("rubber")
            .sound("quack")
            .wingsState(DuckWingsState.ACTIVE);

    @DataProvider(name = "duckListWithActiveWings")
    public Object[][] DuckDataProvider() {
        return new Object[][]{
                {duckProperties1,"{\n" + "\"message\": \"I'm flying\"\n" + "}",null},
                {duckProperties2,"{\n" + "\"message\": \"I'm flying\"\n" + "}",null},
                // 1) Данные для каждого тестового случая ! (каждый тест, где есть провайдер, прогоняется несколько раз).
                // 2) Значения "response" должны иметь JSON структуру, т.к. метод валидации (validateResponseFromString)
                //    ожидает MediaType.APPLICATION_JSON_VALUE.
        };
    }

    @Test(description = "Проверка, что уточка с активными крыльями может летать", dataProvider = "duckListWithActiveWings")
    @CitrusTest
    @CitrusParameters({"payload", "response", "runner"})
    // /duck-service-1.0.2/ Failed to validate JSON text. Values not equal for entry: 'message', expected ' I'm flying' but was 'I am flying :)'
    public void DuckFlyWithActiveWings(DuckPropertiesPayload payload, String response, @Optional @CitrusResource TestCaseRunner runner) {
        // 1) createDuck(runner, payload); Включить и переписать код, если нужно создавать уток с помощью эндпоинта
        //    /api/duck/create и payload. Иначе, будут создаваться через БД.
        String id = getUniqueId(runner);
        runner.variable("duckId", id);
        runner.$(doFinally().actions(ctx -> dbQuery(runner,
                "DELETE FROM DUCK WHERE ID=${duckId}")));

        dbQuery(runner, "INSERT INTO DUCK (ID, COLOR, HEIGHT, MATERIAL, SOUND, WINGS_STATE)\n" +
                "VALUES (${duckId}, '" +payload.color()+ "', '" +payload.height()+ "', '" +payload.material()+ "', '"
                + payload.sound()+ "', '" +payload.wingsState()+ "');");

        duckFly(runner, "${duckId}");
        validateResponseFromString(runner, response);
    }

    @Test(description = "Проверка, что уточка со связанными крыльями НЕ может летать")
    @CitrusTest
    // /duck-service-1.0.2/ Values not equal for entry: 'message', expected 'I can’t fly' but was 'I can not fly :C'
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
