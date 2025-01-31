package autotests.tests.DuckActionController;

import autotests.clients.DuckActionsClient;
import autotests.payloads.MessageStringPayload;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

import static com.consol.citrus.container.FinallySequence.Builder.doFinally;

public class SwimTest extends DuckActionsClient {

    // ошибка 404 (лапки не найдены), хотя уточка существует в бд. ОР: 200 OK
    @Test(description = "Проверка, что уточка, существующая в бд (id), может плавать")
    @CitrusTest
    public void DuckSwimWithExistingID(@Optional @CitrusResource TestCaseRunner runner) {
        String id = getUniqueId(runner);
        runner.variable("duckId", id);
        runner.$(doFinally().actions(ctx -> dbQuery(runner,
                "DELETE FROM DUCK WHERE ID=${duckId}")));
        // создание утки
        dbQuery(runner, "INSERT INTO DUCK (ID, COLOR, HEIGHT, MATERIAL, SOUND, WINGS_STATE)\n" +
                "VALUES (${duckId}, 'string', 0.15, 'wood', 'quack', 'ACTIVE');");
        duckTryToSwim(runner, "${duckId}");
        validateResponseFromPayload(runner, new MessageStringPayload().message("{\n" +
                "\"message\": \"I’m swimming\"\n" +
                "}"));
    }

    // Ошибка 404. ОР: 200 OK
    @Test(description = "Проверка, что уточка, несуществующая в бд (нет такого id), не будет плавать")    @CitrusTest
    public void DuckSwimWithInvalidID(@Optional @CitrusResource TestCaseRunner runner) {
        String id = getUniqueId(runner); // несуществующий id
        runner.variable("duckId", id);
        runner.$(doFinally().actions(ctx -> dbQuery(runner,
                "DELETE FROM DUCK WHERE ID=${duckId}")));

        duckTryToSwim(runner, id);
        validateResponseFromString(runner, "{\n" +
                "\"message\": \"I’m swimming\"\n" +
                "}");
    }
}
