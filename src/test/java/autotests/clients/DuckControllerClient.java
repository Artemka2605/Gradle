package autotests.clients;

import autotests.BaseTest;
import autotests.EndpointConfig;
import autotests.payloads.DuckPropertiesPayload;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import io.qameta.allure.Step;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {EndpointConfig.class})
public class DuckControllerClient extends BaseTest {
    @Step("Эндпоинт для создания уточки")
    public void createDuck(@CitrusResource TestCaseRunner runner, DuckPropertiesPayload payload)
    {
        sendPostRequest(runner, duckService,
                "/api/duck/create", payload);
    }
    // Можно добавить эндпоинт на получение всех id

    @Step("Эндпоинт для удаления уточки")
    public void deleteDuck(@CitrusResource TestCaseRunner runner, String id) {
        sendDeleteRequest(runner, duckService,
                "/api/duck/delete?id="+id);
    }

    @Step("Эндпоинт для обновления параметров уточки")
    public void updateDuck(@CitrusResource TestCaseRunner runner, DuckPropertiesPayload payload) {
        sendPutRequest(runner, duckService,
                "/api/duck/update?color=" +payload.color()+ "&height=" +payload.height()+ "&id=${duckId}"+
                        "&material=" +payload.material()+ "&sound="+payload.sound() );
    }

    // Дублировал из DuckActionControllerClient, чтобы можно было использовать в UpdateTest, который наследуется от DuckControllerClient
    @Step("Эндпоинт для показа характеристик уточки")
    public void showDuckProperties(@CitrusResource TestCaseRunner runner, String id) {
        sendGetRequest(runner,
                duckService,
                "/api/duck/action/properties?id="+id);
    }
}
