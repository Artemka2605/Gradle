package autotests.tests.DuckActionController;

import autotests.clients.DuckActionControllerClient;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

import static com.consol.citrus.container.FinallySequence.Builder.doFinally;

@Epic("Тесты на duck-action-controller")
@Feature("Эндпоинт /api/duck/action/quack")
public class QuackTest extends DuckActionControllerClient {
    // 1) /duck-service-1.0.2/ Number of JSON entries not equal for element: '$.', expected '1' but was '5'
    // 2) Значение "height" в ответе возвращается умноженным на 100. В БД сохраняется корректно.
    @Test(description = "Проверка, что уточка с корректным нечётным id и корректным звуком (quack) будет крякать")
    @CitrusTest
    public void DuckQuackWithOddIdAndCorrectSound(@Optional @CitrusResource TestCaseRunner runner) {
        String id, repetitionCount = "1", soundCount = "1";
        runner.$(doFinally().actions(ctx -> dbQuery(runner,
                "DELETE FROM DUCK WHERE ID=${duckId}")));

        do{
            id = getUniqueId(runner);
        } while (Integer.parseInt(id) % 2 != 1);
        runner.variable("duckId", id);
        dbQuery(runner, "INSERT INTO DUCK (ID, COLOR, HEIGHT, MATERIAL, SOUND, WINGS_STATE)\n" +
                "VALUES (${duckId}, 'string', 0.15, 'rubber', 'quack', 'UNDEFINED');");

        duckQuack(runner, "${duckId}", repetitionCount, soundCount);
        validateResponseFromString(runner, "{\n" +
                "\"sound\": \"quack\"\n" +
                "}");
    }

    //   Раньше - Значение "звук" в ответе на duckQuack равно moo, а ожидается quack (ТЕСТ ПРОВАЛЕН)
    // 1) //duck-service-1.0.2/ теперь: Number of JSON entries not equal for element: '$.', expected '1' but was '5'
    // 2) Значение "height" в ответе возвращается умноженным на 100. В БД сохраняется корректно.
    @Test(description = "Проверка, что уточка с корректным чётным id и корректным звуком (quack) будет крякать")
    @CitrusTest
    public void DuckQuackWithEvenIdAndCorrectSound(@Optional @CitrusResource TestCaseRunner runner) {
        String id, repetitionCount = "1", soundCount = "1";
        runner.$(doFinally().actions(ctx -> dbQuery(runner,
                "DELETE FROM DUCK WHERE ID=${duckId}")));

        do{
            id = getUniqueId(runner);
        } while (Integer.parseInt(id) % 2 != 0);
        runner.variable("duckId", id);
        dbQuery(runner, "INSERT INTO DUCK (ID, COLOR, HEIGHT, MATERIAL, SOUND, WINGS_STATE)\n" +
                "VALUES (${duckId}, 'string', 0.15, 'rubber', 'quack', 'UNDEFINED');");

        duckQuack(runner, "${duckId}", repetitionCount, soundCount);
        validateResponseFromResources(runner, "DuckActionsTest/successfulQuack.json");
    }
}
