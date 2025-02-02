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
@Feature("Эндпоинт /api/duck/create")
public class CreateTest extends DuckControllerClient {
    // Можно сделать общий json файл с тестовыми данными. Затем на его основе создавать экземпляры модели класса DuckPropertiesPayload
    // Или как лучше разделить тестовые данные и тест ?
    DuckPropertiesPayload duckPropertiesRubber1 = new DuckPropertiesPayload()
            .color("string")
            .height(0.01)
            .material("rubber")
            .sound("quack")
            .wingsState(DuckWingsState.ACTIVE);
    DuckPropertiesPayload duckPropertiesRubber2 = new DuckPropertiesPayload()
            .color("blue")
            .height(0.5)
            .material("rubber")
            .sound("quack")
            .wingsState(DuckWingsState.ACTIVE);
    DuckPropertiesPayload duckPropertiesRubber3 = new DuckPropertiesPayload()
            .color("orange")
            .height(22.15)
            .material("rubber")
            .sound("quack")
            .wingsState(DuckWingsState.FIXED);
    DuckPropertiesPayload duckPropertiesRubber4 = new DuckPropertiesPayload()
            .color("red")
            .height(17)
            .material("rubber")
            .sound("quack")
            .wingsState(DuckWingsState.ACTIVE);
    DuckPropertiesPayload duckPropertiesRubber5 = new DuckPropertiesPayload()
            .color("brown")
            .height(2215)
            .material("rubber")
            .sound("quack")
            .wingsState(DuckWingsState.FIXED);

    DuckPropertiesPayload duckPropertiesWood1 = new DuckPropertiesPayload()
            .color("green")
            .height(0.00001)
            .material("wood")
            .sound("quack")
            .wingsState(DuckWingsState.UNDEFINED);
    DuckPropertiesPayload duckPropertiesWood2 = new DuckPropertiesPayload()
            .color("string")
            .height(0.15)
            .material("wood")
            .sound("quackqua")
            .wingsState(DuckWingsState.ACTIVE);

    // 1) В DataProvider Данные для каждого тестового случая ! (каждый тест, где есть провайдер, прогоняется несколько раз).
    // 2) Значения "response" должны иметь JSON структуру, т.к. метод валидации (validateResponseFromString)
    //    ожидает MediaType.APPLICATION_JSON_VALUE.
    @DataProvider(name = "duckListWithRubberMaterial")
    public Object[][] DuckDataProviderForRubberMaterial() {
        return new Object[][]{
                {duckPropertiesRubber1, null},
                {duckPropertiesRubber2, null},
                {duckPropertiesRubber3, null},
                {duckPropertiesRubber4, null},
                {duckPropertiesRubber5, null},
        };
    }
    @DataProvider(name = "duckListWithWoodMaterial")
    public Object[][] DuckDataProviderWithWoodMaterial() {
        return new Object[][]{
                {duckPropertiesWood1, null},
                {duckPropertiesWood2, null},
        };
    }

    // Ошибка на валидации: Number of JSON entries not equal for element: '$.', expected '5' but was '6'
    // Возникает, потому что в ответе приходит дополнительное значение id, однако, согласно документации для сервиса
    // "duck-service-1.0.0", ответ на метод create такого не предусматривает.
    @Test(description = "Проверка, что создаётся уточка с материалом rubber", dataProvider = "duckListWithRubberMaterial")
    @CitrusTest
    @CitrusParameters({"payload", "runner"})
    public void DuckCreateWithRubberMaterial(DuckPropertiesPayload payload, @Optional @CitrusResource TestCaseRunner runner) {
        String id = getUniqueId(runner);
        runner.variable("duckId", id);
        runner.$(doFinally().actions(ctx -> dbQuery(runner,
                "DELETE FROM DUCK WHERE ID=${duckId}")));

        createDuck(runner, payload);
        validateResponseFromPayload(runner, payload);
    }

    // Ошибка на валидации: Number of JSON entries not equal for element: '$.', expected '5' but was '6'
    // Возникает, потому что в ответе приходит дополнительное значение id, однако, согласно документации для сервиса
    // "duck-service-1.0.0", ответ на метод create такого не предусматривает.
    @Test(description = "Проверка, что создаётся уточка с материалом wood", dataProvider = "duckListWithWoodMaterial")
    @CitrusTest
    @CitrusParameters({"payload", "runner"})
    public void DuckCreateWithWoodMaterial(DuckPropertiesPayload payload, @Optional @CitrusResource TestCaseRunner runner) {
        String id = getUniqueId(runner);
        runner.variable("duckId", id);
        runner.$(doFinally().actions(ctx -> dbQuery(runner,
                "DELETE FROM DUCK WHERE ID=${duckId}")));

        createDuck(runner, payload);
        validateResponseFromPayload(runner, payload);
    }
}
