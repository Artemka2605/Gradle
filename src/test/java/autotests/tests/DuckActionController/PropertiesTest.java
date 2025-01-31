package autotests.tests.DuckActionController;

import autotests.clients.DuckActionsClient;
import autotests.payloads.DuckCreatePayload;
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
        do {
            id = getUniqueId(runner);
            runner.variable("duckId", id);
            // создание утки
            dbQuery(runner, "INSERT INTO DUCK (ID, COLOR, HEIGHT, MATERIAL, SOUND, WINGS_STATE)\n" +
                    "VALUES (${duckId}, 'string', 0.15, 'wood', 'quack', 'UNDEFINED');");
            if (Integer.parseInt(runner.variable("${duckId}", id)) % 2 == 0){
                break;
            }
            dbQuery(runner,
                    "DELETE FROM DUCK WHERE ID=${duckId}"
            );
        } while (Integer.parseInt(runner.variable("${duckId}", id)) % 2 != 0);
        showDuckProperties(runner, "${duckId}");
        // в ответе приходит пустой ответ (пустой json) и валидация ответа проваливается
        DuckCreatePayload duck = new DuckCreatePayload();
        validateResponseFromPayload(runner, duck);
    }

    // в ответе приходит пустой ответ (пустой json) и валидация ответа проваливается вместе с тестом.
    @Test(description = "Проверка, что приходит ответ с характеристиками уточки (кроме id) с нечётным ID и материалом rubber")
    @CitrusTest
    public void DuckPropertiesWithOddId(@Optional @CitrusResource TestCaseRunner runner) {
        String id;
        runner.$(doFinally().actions(ctx -> dbQuery(runner,
                "DELETE FROM DUCK WHERE ID=${duckId}")));
        do {
            id = getUniqueId(runner);
            runner.variable("duckId", id);
            // создание утки
            dbQuery(runner, "INSERT INTO DUCK (ID, COLOR, HEIGHT, MATERIAL, SOUND, WINGS_STATE)\n" +
                    "VALUES (${duckId}, 'string', 0.15, 'rubber', 'quack', 'UNDEFINED');");
            if (Integer.parseInt(runner.variable("${duckId}", id)) % 2 == 1){
                break;
            }
            dbQuery(runner,
                    "DELETE FROM DUCK WHERE ID=${duckId}"
            );
        } while (Integer.parseInt(runner.variable("${duckId}", id)) % 2 != 1);
        showDuckProperties(runner, "${duckId}");
        // в ответе приходит пустой ответ (пустой json) и валидация ответа проваливается
        DuckCreatePayload duck = new DuckCreatePayload();
        validateResponseFromPayload(runner, duck);
    }
}
