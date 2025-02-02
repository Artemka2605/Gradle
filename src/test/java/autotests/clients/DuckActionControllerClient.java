package autotests.clients;

import autotests.BaseTest;
import autotests.EndpointConfig;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.http.client.HttpClient;
import io.qameta.allure.Step;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {EndpointConfig.class})
public class DuckActionControllerClient extends BaseTest {
    // здесь лежат универсальные методы (создать, удалить уточку, получить айди)

    @Autowired
    protected HttpClient duckService;

    @Step("Эндпоинт для полёта уточки")
    public void duckFly(@CitrusResource TestCaseRunner runner, String id) {
        sendGetRequest(runner, duckService,
                "/api/duck/action/fly?id="+id);
    }
    @Step("Эндпоинт для показа характеристик уточки")
    public void showDuckProperties(@CitrusResource TestCaseRunner runner, String id) {
        sendGetRequest(runner,
                duckService,
                "/api/duck/action/properties?id="+id);
    }
    @Step("Эндпоинт чтобы уточка крякала")
    public void duckQuack(TestCaseRunner runner, String id, String repetitionCount, String soundCount) {
        sendGetRequest(runner, duckService,
                "/api/duck/action/properties?id="+id+"&repetitionCount="+repetitionCount+"&soundCount="+soundCount);
    }
    @Step("Эндпоинт чтобы уточка плыла")
    public void duckTryToSwim(TestCaseRunner runner, String id) {
        sendGetRequest(runner, duckService,
                "/api/duck/action/swim?id="+id);
    }
}