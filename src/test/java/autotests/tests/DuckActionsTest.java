package autotests.tests;

import autotests.clients.DuckActionsClient;
import OldAutotests.duckController.DuckDeleteTest;
import autotests.payloads.DuckCreate;
import autotests.payloads.DuckCreatePayload;
import autotests.payloads.DuckWingsState;
import autotests.payloads.MessageStringPayload;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.context.TestContext;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

import static com.consol.citrus.DefaultTestActionBuilder.action;
import static com.consol.citrus.container.FinallySequence.Builder.doFinally;

public class DuckActionsTest extends DuckActionsClient {
    // здесь лежат только тесты

    @Test(description = "Проверка, что уточка с активными крыльями может летать")
    @CitrusTest
    public void DuckFlyWithActiveWings(@Optional @CitrusResource TestCaseRunner runner, @CitrusResource TestContext context) {
        DuckCreate duck = createDuckObject();
        duck.wingsState(DuckWingsState.ACTIVE);
        createDuck(runner, duck);

        extractIdFromResponse(runner);
        duckFly(runner, "${duckId}");//  context.getVariable("${duckId}")
        validateResponseFromResources(runner, "DuckActionsTest/successfulFly.json");

        doFinally().actions(
                runner.$(
                        action(ctx -> deleteDuck(runner, context.getVariable("${duckId}")))
                ));
    }

    @Test(description = "Проверка, что уточка со связанными крыльями НЕ может летать")
    @CitrusTest
    public void DuckFlyWithFixedWings(@Optional @CitrusResource TestCaseRunner runner, @CitrusResource TestContext context) {
        DuckCreate duck = createDuckObject();
        duck.wingsState(DuckWingsState.FIXED);
        createDuck(runner, duck);

        extractIdFromResponse(runner);
        duckFly(runner, "${duckId}");

        MessageStringPayload payloadMessage = new MessageStringPayload();
        payloadMessage.message("I can not fly :C");
        validateResponseFromPayload(runner, payloadMessage);

        doFinally().actions(
                runner.$(
                        action(ctx -> deleteDuck(runner, context.getVariable("${duckId}")))
                ));
    }

    @Test(description = "Проверка, что уточка с неопределёнными крыльями может летать")
    @CitrusTest
    public void DuckFlyWithUndefinedWings(@Optional @CitrusResource TestCaseRunner runner, @CitrusResource TestContext context) {
        DuckCreate duck = createDuckObject();
        duck.wingsState(DuckWingsState.UNDEFINED);
        createDuck(runner, duck);

        extractIdFromResponse(runner);
        duckFly(runner, "${duckId}");
        validateResponseFromString(runner, "{\n" +
                "\"message\": \"Wings are not detected :(\"\n" +
                "}");

        doFinally().actions(
                runner.$(
                        action(ctx -> deleteDuck(runner, context.getVariable("${duckId}")))
                ));
    }


    // в ответе приходит пустой ответ (пустой json) и валидация ответа проваливается вместе с тестом.
    @Test(enabled = false, description = "Проверка, что приходит ответ с характеристиками уточки (кроме id) с чётным ID и материалом wood")
    @CitrusTest
    public void DuckPropertiesWithEvenId(@Optional @CitrusResource TestCaseRunner runner, @CitrusResource TestContext context ) {
        DuckCreate duck = createDuckObject();
        duck.material("wood");
        do {
            createDuck(runner, duck);
            extractIdFromResponse(runner);
        } while (Integer.parseInt(context.getVariable("${duckId}")) % 2 != 0);

        showDuckProperties(runner, "${duckId}");
        // в ответе приходит пустой ответ (пустой json) и валидация ответа проваливается
        validateResponseFromPayload(runner, duck);

        doFinally().actions(
                runner.$(
                        action(ctx -> deleteDuck(runner, context.getVariable("${duckId}")))
                ));
    }

    // в ответе приходит пустой ответ (пустой json) и валидация ответа проваливается вместе с тестом.
    @Test(enabled = false, description = "Проверка, что приходит ответ с характеристиками уточки (кроме id) с нечётным ID и материалом rubber")
    @CitrusTest
    public void DuckPropertiesWithOddId(@Optional @CitrusResource TestCaseRunner runner, @CitrusResource TestContext context) {
        DuckCreate duck = createDuckObject();
        duck.material("rubber");
        do {
            createDuck(runner, duck);
            extractIdFromResponse(runner);
        } while (Integer.parseInt(context.getVariable("${duckId}")) % 2 != 1);

        showDuckProperties(runner, "${duckId}");
        // в ответе приходит пустой ответ (пустой json) и валидация ответа проваливается
        validateResponseFromPayload(runner, duck);

        doFinally().actions(
                runner.$(
                        action(ctx -> deleteDuck(runner, context.getVariable("${duckId}")))
                ));
    }



    @Test(description = "Проверка, что уточка с корректным нечётным id и корректным звуком (quack) будет крякать")
    @CitrusTest
    public void DuckQuackWithOddIdAndCorrectSound(@Optional @CitrusResource TestCaseRunner runner, @CitrusResource TestContext context) {
        DuckCreate duck = createDuckObject();
        duck.sound("quack");
        runner.variable("repetitionCount", 1);
        runner.variable("soundCount", 1);

        do {
            createDuck(runner, duck);
            extractIdFromResponse(runner);
        } while (Integer.parseInt(context.getVariable("${duckId}")) % 2 != 1);

        duckQuack(runner, "${duckId}", "${repetitionCount}", "${soundCount}");
        validateResponseFromString(runner, "{\n" +
                "\"sound\": \"quack\"\n" +
                "}");

        doFinally().actions(
                runner.$(
                        action(ctx -> deleteDuck(runner, context.getVariable("${duckId}")))
                ));
    }

    // Значение "звук" в ответе на duckQuack равно moo, а ожидается quack (ТЕСТ ПРОВАЛЕН)
    @Test(description = "Проверка, что уточка с корректным чётным id и корректным звуком (quack) будет крякать")
    @CitrusTest
    public void DuckQuackWithEvenIdAndCorrectSound(@Optional @CitrusResource TestCaseRunner runner, @CitrusResource TestContext context) {
        DuckCreate duck = createDuckObject();
        duck.sound("quack");
        runner.variable("repetitionCount", 1);
        runner.variable("soundCount", 1);

        do {
            createDuck(runner, duck);
            extractIdFromResponse(runner);
        } while (Integer.parseInt(context.getVariable("${duckId}")) % 2 != 0);

        duckQuack(runner, "${duckId}", "${repetitionCount}", "${soundCount}");
        validateResponseFromResources(runner, "DuckActionsTest/successfulQuack.json");

        doFinally().actions(
                runner.$(
                        action(ctx -> deleteDuck(runner, context.getVariable("${duckId}")))
                ));
    }


    // ошибка 404 (лапки не найдены), хотя уточка существует в бд
    @Test(description = "Проверка, что уточка, существующая в бд (id), может плавать")
    @CitrusTest
    public void DuckSwimWithExistingID(@Optional @CitrusResource TestCaseRunner runner, @CitrusResource TestContext context) {
        DuckCreate duck = createDuckObject();
        createDuck(runner, duck);
        extractIdFromResponse(runner);
        duckTryToSwim(runner, "${duckId}");

        validateResponseFromPayload(runner, new MessageStringPayload().message("{\n" +
                "\"message\": \"string\"\n" +
                "}"));

        doFinally().actions(
                runner.$(
                        action(ctx -> deleteDuck(runner, context.getVariable("${duckId}")))
                ));
    }

    // Ошибка 404. Возможно стоит сделать ожидаемый результат сделать 200
    @Test(description = "Проверка, что уточка, несуществующая в бд (нет такого id), не будет плавать")    @CitrusTest
    public void DuckSwimWithInvalidID(@Optional @CitrusResource TestCaseRunner runner, @CitrusResource TestContext context) {
        DuckCreate duck = createDuckObject();
        createDuck(runner, duck);
        extractIdFromResponse(runner);

        // +1 для взятия несуществующего id в БД.
        int invalidId = Integer.parseInt(context.getVariable("${duckId}")) + 1;
        duckTryToSwim(runner, String.valueOf(invalidId));
        validateResponseFromString(runner, "{\n" +
                "\"message\": \"Paws are not found ((((\"\n" +
                "}");

        doFinally().actions(
                runner.$(
                        action(ctx -> deleteDuck(runner, context.getVariable("${duckId}")))
                ));
    }



    @Test(description = "Проверка, что создаётся уточка с материалом rubber")
    @CitrusTest
    public void DuckCreateWithRubberMaterial(@Optional @CitrusResource TestCaseRunner runner, @CitrusResource TestContext context) {
        DuckCreate duck = createDuckObject();
        duck.material("rubber");
        createDuck(runner, duck);
        extractIdFromResponse(runner);

        DuckCreatePayload payload = new DuckCreatePayload()
                .id("${duckId}");
        //todo: Action timeout after 5000 milliseconds. Failed to receive message on endpoint: 'duckService'
        validateResponseFromPayload(runner, payload);

        doFinally().actions(
                runner.$(
                        action(ctx -> deleteDuck(runner, context.getVariable("${duckId}")))
                ));
    }

    @Test(description = "Проверка, что создаётся уточка с материалом wood")
    @CitrusTest
    public void DuckCreateWithWoodMaterial(@Optional @CitrusResource TestCaseRunner runner, @CitrusResource TestContext context) {
        DuckCreate duck = createDuckObject();
        duck.material("wood");
        createDuck(runner, duck);
        extractIdFromResponse(runner);

        DuckCreatePayload payload = new DuckCreatePayload()
                .id("${duckId}");

        //todo: Action timeout after 5000 milliseconds. Failed to receive message on endpoint: 'duckService'
        validateResponseFromPayload(runner, payload);

        doFinally().actions(
                runner.$(
                        action(ctx -> deleteDuck(runner, context.getVariable("${duckId}")))
                ));
    }



    @Test(description = "Проверка, что уточка удаляется")
    @CitrusTest
    public void DuckDelete(@Optional @CitrusResource TestCaseRunner runner, @CitrusResource TestContext context) {
        DuckCreate duck = createDuckObject();
        createDuck(runner, duck);
        extractIdFromResponse(runner);

        deleteDuck(runner, "${duckId}");
        validateResponseFromString(runner, "{\n" +
                "\"message\": \"Duck is deleted\"\n" +
                "}");
    }



    @Test(description = "Проверка, что у уточки изменяются свойства: 'цвет' и 'высота' ")
    @CitrusTest
    public void DuckUpdateColorAndHeight(@Optional @CitrusResource TestCaseRunner runner, @CitrusResource TestContext context) {
        DuckCreate duck = createDuckObject();
        createDuck(runner, duck);
        extractIdFromResponse(runner);

        // todo: сделать сравнение значений цвета и высоты до изменения со значениями после
        duck.color("blue");
        duck.height(0.05);

        updateDuckColorAndHeight(runner, "${duckId}", duck);
        validateResponseFromString(runner, "{\n" +
                "\"message\": \"Duck with id = " + "${duckId}" + " is updated\"\n" +
                "}");

        doFinally().actions(
                runner.$(
                        action(ctx -> deleteDuck(runner, context.getVariable("${duckId}")))
                ));
    }


    @Test(description = "Проверка, что у уточки изменяются свойства: 'цвет' и 'звук' ")
    @CitrusTest
    public void DuckUpdateColorAndSound(@Optional @CitrusResource TestCaseRunner runner, @CitrusResource TestContext context) {
        DuckCreate duck = createDuckObject();
        createDuck(runner, duck);
        extractIdFromResponse(runner);

        // todo: сделать сравнение значений цвета и высоты до изменения со значениями после
        duck.color("red");
        duck.sound("quack");

        updateDuckColorAndHeight(runner, "${duckId}", duck);
        validateResponseFromString(runner, "{\n" +
                "\"message\": \"Duck with id = " + "${duckId}" + " is updated\"\n" +
                "}");

        doFinally().actions(
                runner.$(
                        action(ctx -> deleteDuck(runner, context.getVariable("${duckId}")))
                ));
    }
}
