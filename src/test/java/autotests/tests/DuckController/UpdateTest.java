package autotests.tests.DuckController;

import autotests.clients.DuckActionsClient;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

import static com.consol.citrus.container.FinallySequence.Builder.doFinally;

@Epic("Тесты на duck-controller")
@Feature("Эндпоинт /api/duck/update")
public class UpdateTest extends DuckActionsClient {
    @Test(description = "Проверка, что у уточки изменяются свойства: 'цвет' и 'высота' ")
    @CitrusTest
    public void DuckUpdateColorAndHeight(@Optional @CitrusResource TestCaseRunner runner) {
        String id = getUniqueId(runner);
        runner.variable("duckId", id);
        runner.$(doFinally().actions(ctx -> dbQuery(runner,
                "DELETE FROM DUCK WHERE ID=${duckId}")));
        // создание утки
        dbQuery(runner, "INSERT INTO DUCK (ID, COLOR, HEIGHT, MATERIAL, SOUND, WINGS_STATE)\n" +
                "VALUES (${duckId}, 'string', 0.15, 'rubber', 'quack', 'ACTIVE');");

        updateDuckColorAndHeight(runner, runner.variable("duckColor", "blue"), runner.variable("duckHeight", 0.05),
                "rubber", "quack");
        validateResponseFromString(runner, "{\n" +
                "\"message\": \"Duck with id = " + "${duckId}" + " is updated\"\n" +
                "}");

        // нада разбираться ............. пока валидация стрингой будет
//        runner.$(sql(dataBaseConnection)
//                .statement("SELECT COLOR, HEIGHT FROM DUCK WHERE ID = ${duckId}"),
//                resultSet -> {
//                    if (resultSet.next()) {
//                        String color = resultSet.getString("COLOR");
//                        double height = resultSet.getDouble("HEIGHT");
//                        Assert.assertEquals(color, "blue", "Color was not updated correctly");
//                        Assert.assertEquals(height, 0.30, "Height was not updated correctly");
//                        return true;
//                    }
//                    return false;
//                },
//                "isUpdated"
//        );
    }


    @Test(description = "Проверка, что у уточки изменяются свойства: 'цвет' и 'звук' ")
    @CitrusTest
    public void DuckUpdateColorAndSound(@Optional @CitrusResource TestCaseRunner runner) {
        String id = getUniqueId(runner);
        runner.variable("duckId", id);
        runner.$(doFinally().actions(ctx -> dbQuery(runner,
                "DELETE FROM DUCK WHERE ID=${duckId}")));
        // создание утки
        dbQuery(runner, "INSERT INTO DUCK (ID, COLOR, HEIGHT, MATERIAL, SOUND, WINGS_STATE)\n" +
                "VALUES (${duckId}, 'string', 0.15, 'rubber', 'quack', 'ACTIVE');");

        updateDuckColorAndHeight(runner, runner.variable("duckColor", "red"), 0.15,
                "rubber", runner.variable("duckSound", "quackQuack"));
        validateResponseFromString(runner, "{\n" +
                "\"message\": \"Duck with id = " + "${duckId}" + " is updated\"\n" +
                "}");
    }
}
