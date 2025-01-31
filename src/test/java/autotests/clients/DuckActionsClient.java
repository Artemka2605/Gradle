package autotests.clients;

import autotests.BaseTest;
import autotests.EndpointConfig;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.http.client.HttpClient;
import io.qameta.allure.Step;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static com.consol.citrus.http.actions.HttpActionBuilder.http;

@ContextConfiguration(classes = {EndpointConfig.class})
public class DuckActionsClient extends BaseTest {
    // здесь лежат универсальные методы (создать, удалить уточку, получить айди, валидация ответа)

    @Autowired
    protected HttpClient duckService;

    @Step("Эндпоинт для полёта уточки")
    public void duckFly(@CitrusResource TestCaseRunner runner, String id) {
        runner.$(http()
                .client(duckService)
                .send()
                .get("/api/duck/action/fly")
                .queryParam("id", id)
        );
    }
    @Step("Эндпоинт для показа характеристик уточки")
    public void showDuckProperties(@CitrusResource TestCaseRunner runner, String id) {
        runner.$(http()
                .client(duckService)
                .send()
                .get("/api/duck/action/properties")
                .queryParam("id", id)
        );
    }
    @Step("Эндпоинт чтобы уточка крякала")
    public void duckQuack(TestCaseRunner runner, String id, String repetitionCount, String soundCount) {
        runner.$(http()
                .client(duckService)
                .send()
                .get("/api/duck/action/quack")
                .queryParam("id", id)
                .queryParam("repetitionCount", repetitionCount)
                .queryParam("soundCount", soundCount)
        );
    }
    @Step("Эндпоинт чтобы уточка плыла")
    public void duckTryToSwim(TestCaseRunner runner, String id) {
        runner.$(http()
                .client(duckService)
                .send()
                .get("/api/duck/action/swim")
                .queryParam("id", id)
        );
    }
}
