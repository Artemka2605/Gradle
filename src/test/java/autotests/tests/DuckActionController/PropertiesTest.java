package autotests.tests.DuckActionController;

import autotests.clients.DuckActionsClient;
import autotests.payloads.DuckPropertiesPayload;
import autotests.payloads.DuckWingsState;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

import static com.consol.citrus.container.FinallySequence.Builder.doFinally;

@Epic("Тесты на duck-action-controller")
@Feature("Эндпоинт /api/duck/action/properties")
public class PropertiesTest extends DuckActionsClient {
    // в ответе приходит пустой ответ (пустой json) и валидация ответа проваливается вместе с тестом.
    @Test(description = "Проверка, что приходит ответ с характеристиками уточки (кроме id) с чётным ID и материалом wood")
    @CitrusTest
    public void DuckPropertiesWithEvenId(@Optional @CitrusResource TestCaseRunner runner) {
        String id;
        runner.$(doFinally().actions(ctx -> dbQuery(runner,
                "DELETE FROM DUCK WHERE ID=${duckId}")));
        do{
            id = getUniqueId(runner);
        } while (Integer.parseInt(id) % 2 != 0);

        // Переменные для уточки. Пытался сделать их в одном месте.
        runner.variable("duckId", id);
        runner.variable("color", "string");
        runner.variable("height", 0.15);
        runner.variable("material", "wood");
        runner.variable("sound", "quack");
        runner.variable("wings_state", DuckWingsState.UNDEFINED);
        // создание утки
        dbQuery(runner, "INSERT INTO DUCK (ID, COLOR, HEIGHT, MATERIAL, SOUND, WINGS_STATE)\n" +
                "VALUES (${duckId}, '${color}', ${height}, '${material}', '${sound}', '${wings_state}');");

        showDuckProperties(runner, "${duckId}");
        // в ответе приходит пустой ответ (пустой json) и валидация ответа проваливается
        DuckPropertiesPayload duck = new DuckPropertiesPayload()
                .color("${color}")
                .height(Double.parseDouble(runner.variable("height", "${height}")))
                .material("${material}")
                .sound("${sound}")
                .wingsState(DuckWingsState.valueOf(runner.variable("wings_state", "${wings_state}")));
        validateResponseFromPayload(runner, duck);
    }
    //  в ответе приходит значение высоты умноженное на 100 и валидация проваливается.
    @Test(description = "Проверка, что приходит ответ с характеристиками уточки (кроме id) с нечётным ID и материалом rubber")
    @CitrusTest
    public void DuckPropertiesWithOddId(@Optional @CitrusResource TestCaseRunner runner) {
        String id;
        runner.$(doFinally().actions(ctx -> dbQuery(runner,
                "DELETE FROM DUCK WHERE ID=${duckId}")));
        do{
            id = getUniqueId(runner);
        } while (Integer.parseInt(id) % 2 != 1);

        // Переменные для уточки. Пытался сделать их в одном месте.
        runner.variable("duckId", id);
        runner.variable("color", "string");
        runner.variable("height", 0.15);
        runner.variable("material", "rubber");
        runner.variable("sound", "quack");
        runner.variable("wings_state", DuckWingsState.UNDEFINED);

        // создание утки
        dbQuery(runner, "INSERT INTO DUCK (ID, COLOR, HEIGHT, MATERIAL, SOUND, WINGS_STATE)\n" +
                "VALUES (${duckId}, '${color}', ${height}, '${material}', '${sound}', '${wings_state}');");

        showDuckProperties(runner, "${duckId}");
        DuckPropertiesPayload duck = new DuckPropertiesPayload()
                .color("${color}")
                .height(Double.parseDouble(runner.variable("height", "${height}")))
                .material("${material}")
                .sound("${sound}")
                .wingsState(DuckWingsState.valueOf(runner.variable("wings_state", "${wings_state}")));
        validateResponseFromPayload(runner, duck);
    }
}
