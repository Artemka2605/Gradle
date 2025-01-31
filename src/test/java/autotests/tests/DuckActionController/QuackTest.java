package autotests.tests.DuckActionController;

import autotests.clients.DuckActionsClient;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

import static com.consol.citrus.container.FinallySequence.Builder.doFinally;

public class QuackTest extends DuckActionsClient {

    @Test(description = "Проверка, что уточка с корректным нечётным id и корректным звуком (quack) будет крякать")
    @CitrusTest
    public void DuckQuackWithOddIdAndCorrectSound(@Optional @CitrusResource TestCaseRunner runner) {
        String id, repetitionCount = "1", soundCount = "1";
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

        duckQuack(runner, "${duckId}", repetitionCount, soundCount);
        validateResponseFromString(runner, "{\n" +
                "\"sound\": \"quack\"\n" +
                "}");
    }

    // Значение "звук" в ответе на duckQuack равно moo, а ожидается quack (ТЕСТ ПРОВАЛЕН)
    @Test(description = "Проверка, что уточка с корректным чётным id и корректным звуком (quack) будет крякать")
    @CitrusTest
    public void DuckQuackWithEvenIdAndCorrectSound(@Optional @CitrusResource TestCaseRunner runner) {
        String id, repetitionCount = "1", soundCount = "1";
        runner.$(doFinally().actions(ctx -> dbQuery(runner,
                "DELETE FROM DUCK WHERE ID=${duckId}")));
        do {
            id = getUniqueId(runner);
            runner.variable("duckId", id);
            // создание утки
            dbQuery(runner, "INSERT INTO DUCK (ID, COLOR, HEIGHT, MATERIAL, SOUND, WINGS_STATE)\n" +
                    "VALUES (${duckId}, 'string', 0.15, 'rubber', 'quack', 'UNDEFINED');");
            if (Integer.parseInt(runner.variable("${duckId}", id)) % 2 == 0){
                break;
            }
            dbQuery(runner,
                    "DELETE FROM DUCK WHERE ID=${duckId}"
            );
        } while (Integer.parseInt(runner.variable("${duckId}", id)) % 2 != 0);
        duckQuack(runner, "${duckId}", repetitionCount, soundCount);
        validateResponseFromResources(runner, "DuckActionsTest/successfulQuack.json");
    }
}
