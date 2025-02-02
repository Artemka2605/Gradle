package autotests.tests.DuckController;

import autotests.clients.DuckControllerClient;
import autotests.payloads.DuckPropertiesPayload;
import autotests.payloads.DuckWingsState;
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

@Epic("Тесты на duck-controller")
@Feature("Эндпоинт /api/duck/update")
public class UpdateTest extends DuckControllerClient {
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

    @DataProvider(name = "duckListForUpdate")
    public Object[][] DuckDataProvider() {
        return new Object[][]{
                {duckProperties1, "{\n" + "\"message\": \"Duck with id = " + "${duckId}" + " is updated\"\n" + "}",null},
                {duckProperties2,"{\n" +  "\"message\": \"Duck with id = " + "${duckId}" + " is updated\"\n" + "}",null},
        };
    }
    // для wood material: Number of JSON entries not equal for element: '$.', expected '5' but was '0'
    // для rubber: Values not equal for entry: 'height', expected '0.55' but was '55.00000000000001'
    @Test(description = "Проверка, что у уточки изменяются свойства: 'цвет' и 'высота' ", dataProvider = "duckListForUpdate")
    @CitrusTest
    @CitrusParameters({"payload", "response", "runner"})
    public void DuckUpdateColorAndHeight(DuckPropertiesPayload payload, String response, @Optional @CitrusResource TestCaseRunner runner) {
        String id = getUniqueId(runner);
        runner.variable("duckId", id);
        runner.$(doFinally().actions(ctx -> dbQuery(runner,
                "DELETE FROM DUCK WHERE ID=${duckId}")));

        dbQuery(runner, "INSERT INTO DUCK (ID, COLOR, HEIGHT, MATERIAL, SOUND, WINGS_STATE)\n" +
                "VALUES (${duckId}, '" +payload.color()+ "', '" +payload.height()+ "', '" +payload.material()+ "', '"
                + payload.sound()+ "', '" +payload.wingsState()+ "');");
        // изменение цвета и высоты
        payload.color("black");
        payload.height(0.55);
        updateDuck(runner, payload);
        validateResponseFromString(runner, response);

        showDuckProperties(runner, "${duckId}"); // Проверяем, что утка после изменений, действительно поменяла свойства
        validateResponseFromPayload(runner, payload);

    }

    // для wood material: Number of JSON entries not equal for element: '$.', expected '5' but was '0'
    // для rubber: Values not equal for entry: 'height', expected '0.55' but was '55.00000000000001'
    @Test(description = "Проверка, что у уточки изменяются свойства: 'цвет' и 'звук' ", dataProvider = "duckListForUpdate" )
    @CitrusTest
    public void DuckUpdateColorAndSound(DuckPropertiesPayload payload, String response, @Optional @CitrusResource TestCaseRunner runner) {
        String id = getUniqueId(runner);
        runner.variable("duckId", id);
        runner.$(doFinally().actions(ctx -> dbQuery(runner,
                "DELETE FROM DUCK WHERE ID=${duckId}")));
        // создание утки
        dbQuery(runner, "INSERT INTO DUCK (ID, COLOR, HEIGHT, MATERIAL, SOUND, WINGS_STATE)\n" +
                "VALUES (${duckId}, '" +payload.color()+ "', '" +payload.height()+ "', '" +payload.material()+ "', '"
                + payload.sound()+ "', '" +payload.wingsState()+ "');");
        // изменение цвета и звука
        payload.color("black");
        payload.sound("quackkk");
        updateDuck(runner, payload);
        validateResponseFromString(runner, response);

        showDuckProperties(runner, "${duckId}"); // Проверяем, что утка после изменений, действительно поменяла свойства
        validateResponseFromPayload(runner, payload);
    }
}
