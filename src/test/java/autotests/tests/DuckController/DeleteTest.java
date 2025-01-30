package autotests.tests.DuckController;

import autotests.clients.DuckActionsClient;
import autotests.payloads.DuckCreate;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

public class DeleteTest extends DuckActionsClient {
    @Test(description = "Проверка, что уточка удаляется")
    @CitrusTest
    public void DuckDelete(@Optional @CitrusResource TestCaseRunner runner) {
        DuckCreate duck = createDuckObject();
        createDuck(runner, duck);
        extractIdFromResponse(runner);

        deleteDuck(runner, "${duckId}");
        validateResponseFromString(runner, "{\n" +
                "\"message\": \"Duck is deleted\"\n" +
                "}");
    }
}
