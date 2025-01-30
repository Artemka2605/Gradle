package autotests.tests.DuckController;

import autotests.clients.DuckActionsClient;
import autotests.payloads.DuckCreatePayload;
import autotests.payloads.DuckWingsState;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

public class DeleteTest extends DuckActionsClient {
    @Test(description = "Проверка, что уточка удаляется")
    @CitrusTest
    public void DuckDelete(@Optional @CitrusResource TestCaseRunner runner) {
        DuckCreatePayload duck = new DuckCreatePayload();
        duck.color("string").height(0.15).material("rubber").sound("quack").wingsState(DuckWingsState.FIXED);
        createDuck(runner, duck);
        extractIdFromResponse(runner);

        deleteDuck(runner);
        validateResponseFromString(runner, "{\n" +
                "\"message\": \"Duck is deleted\"\n" +
                "}");
    }
}
