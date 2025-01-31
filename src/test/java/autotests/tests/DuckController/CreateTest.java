package autotests.tests.DuckController;

import autotests.clients.DuckActionsClient;
import autotests.payloads.DuckCreatePayload;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

import static com.consol.citrus.container.FinallySequence.Builder.doFinally;

public class CreateTest extends DuckActionsClient {

    @Test(description = "Проверка, что создаётся уточка с материалом rubber")
    @CitrusTest
    public void DuckCreateWithRubberMaterial(@Optional @CitrusResource TestCaseRunner runner) {
        String id = getUniqueId(runner);
        runner.variable("duckId", id);
        runner.$(doFinally().actions(ctx -> dbQuery(runner,
                "DELETE FROM DUCK WHERE ID=${duckId}")));
        // создание утки
        dbQuery(runner, "INSERT INTO DUCK (ID, COLOR, HEIGHT, MATERIAL, SOUND, WINGS_STATE)\n" +
                "VALUES (${duckId}, 'string', 0.15, 'rubber', 'quack', 'ACTIVE');");

        DuckCreatePayload duck = new DuckCreatePayload();
        //Ошибка: Failed to get correlation key for 'citrus_message_correlator_duckService'
        validateResponseFromPayload(runner, duck);
    }

    @Test(description = "Проверка, что создаётся уточка с материалом wood")
    @CitrusTest
    public void DuckCreateWithWoodMaterial(@Optional @CitrusResource TestCaseRunner runner) {
        String id = getUniqueId(runner);
        runner.variable("duckId", id);
        runner.$(doFinally().actions(ctx -> dbQuery(runner,
                "DELETE FROM DUCK WHERE ID=${duckId}")));
        // создание утки
        dbQuery(runner, "INSERT INTO DUCK (ID, COLOR, HEIGHT, MATERIAL, SOUND, WINGS_STATE)\n" +
                "VALUES (${duckId}, 'string', 0.15, 'wood', 'quack', 'ACTIVE');");

        DuckCreatePayload duck = new DuckCreatePayload();
        //Ошибка: Failed to get correlation key for 'citrus_message_correlator_duckService'
        validateResponseFromPayload(runner, duck);
    }
}
