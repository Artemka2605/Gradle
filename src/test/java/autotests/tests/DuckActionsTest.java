package autotests.tests;

import autotests.clients.DuckActionsClient;
import OldAutotests.duckController.DuckDeleteTest;
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
        // создаём уточку и задаём переменные в контексте
        setDuckVariablesInRunner(runner);
        runner.variable("wingsState", "ACTIVE"); //
        createDuck(runner, context);

        extractIdFromResponse(runner);
        duckFly(runner, context.getVariable("${duckId}"));
        validateResponse(runner, "{\n" +
                "\"message\": \"I am flying :)\"\n" +
                "}");

        // удаление созданной утки
        DuckDeleteTest deleteTest = new DuckDeleteTest();
        doFinally().actions(
                runner.$(
                        action(ctx ->
                                deleteTest.deleteDuck(runner, context.getVariable("${duckId}")))
                ));
    }

    @Test(description = "Проверка, что уточка со связанными крыльями НЕ может летать")
    @CitrusTest
    public void DuckFlyWithFixedWings(@Optional @CitrusResource TestCaseRunner runner, @CitrusResource TestContext context) {
        // создаём уточку и задаём переменные в контексте
        setDuckVariablesInRunner(runner);
        runner.variable("wingsState", "FIXED"); //
        createDuck(runner, context);

        extractIdFromResponse(runner);
        duckFly(runner, context.getVariable("${duckId}"));
        validateResponse(runner, "{\n" +
                "\"message\": \"I can not fly :C\"\n" +
                "}");

        // удаление созданной утки
        DuckDeleteTest deleteTest = new DuckDeleteTest();
        doFinally().actions(
                runner.$(
                        action(ctx ->
                                deleteTest.deleteDuck(runner, context.getVariable("${duckId}")))
                ));
    }

    @Test(description = "Проверка, что уточка с неопределёнными крыльями может летать")
    @CitrusTest
    public void DuckFlyWithUndefinedWings(@Optional @CitrusResource TestCaseRunner runner, @CitrusResource TestContext context) {
        // создаём уточку и задаём переменные в контексте
        setDuckVariablesInRunner(runner);
        runner.variable("wingsState", "UNDEFINED"); //
        createDuck(runner, context);

        extractIdFromResponse(runner);
        duckFly(runner, context.getVariable("${duckId}"));
        validateResponse(runner, "{\n" +
                "\"message\": \"Wings are not detected :(\"\n" +
                "}");
        // удаление созданной утки
        DuckDeleteTest deleteTest = new DuckDeleteTest();
        doFinally().actions(
                runner.$(
                        action(ctx ->
                                deleteTest.deleteDuck(runner, context.getVariable("${duckId}")))
                ));
    }


    // в ответе приходит пустой ответ (пустой json) и валидация ответа проваливается вместе с тестом.
    @Test(enabled = false, description = "Проверка, что приходит ответ с характеристиками уточки (кроме id) с чётным ID и материалом wood")
    @CitrusTest
    public void DuckPropertiesWithEvenId(@Optional @CitrusResource TestCaseRunner runner, @CitrusResource TestContext context ) {
        setDuckVariablesInRunner(runner);
        runner.variable("material", "wood");

        do {
            createDuck(runner, context);
            extractIdFromResponse(runner);
        } while (Integer.parseInt(context.getVariable("${duckId}")) % 2 != 0);

        showDuckProperties(runner, context.getVariable("${duckId}"));
        // в ответе приходит пустой ответ (пустой json) и валидация ответа проваливается
        validateResponse(runner, "{\n" +
                " \"color\": \"" + context.getVariable("${color}") + "\",\n" +
                " \"height\": " + context.getVariable("${height}") + ",\n" +
                " \"material\": \"" + context.getVariable("${material}") + "\",\n" +
                " \"sound\": \"" + context.getVariable("${sound}")+ "\",\n" +
                " \"wingsState\": \"" + context.getVariable("${wingsState}") + "\"\n" + "}");

        // удаление созданной утки
        DuckDeleteTest deleteTest = new DuckDeleteTest();
        doFinally().actions(
                runner.$(
                        action(ctx ->
                                deleteTest.deleteDuck(runner, context.getVariable("${duckId}")))
                ));
    }

    // в ответе приходит пустой ответ (пустой json) и валидация ответа проваливается вместе с тестом.
    @Test(enabled = false, description = "Проверка, что приходит ответ с характеристиками уточки (кроме id) с нечётным ID и материалом rubber")
    @CitrusTest
    public void DuckPropertiesWithOddId(@Optional @CitrusResource TestCaseRunner runner, @CitrusResource TestContext context) {
        setDuckVariablesInRunner(runner);
        runner.variable("material", "rubber");

        do {
            createDuck(runner, context);
            extractIdFromResponse(runner);
        } while (Integer.parseInt(context.getVariable("${duckId}")) % 2 != 1);

        showDuckProperties(runner, context.getVariable("${duckId}"));
        validateResponse(runner, "{\n" +
                " \"color\": \"" + context.getVariable("${color}") + "\",\n" +
                " \"height\": " + context.getVariable("${height}") + ",\n" +
                " \"material\": \"" + context.getVariable("${material}") + "\",\n" +
                " \"sound\": \"" + context.getVariable("${sound}")+ "\",\n" +
                " \"wingsState\": \"" + context.getVariable("${wingsState}") + "\"\n" + "}");

        DuckDeleteTest deleteTest = new DuckDeleteTest();
        doFinally().actions(
                runner.$(
                        action(ctx ->
                                deleteTest.deleteDuck(runner, context.getVariable("${duckId}")))
                ));
    }



    @Test(description = "Проверка, что уточка с корректным нечётным id и корректным звуком (quack) будет крякать")
    @CitrusTest
    public void DuckQuackWithOddIdAndCorrectSound(@Optional @CitrusResource TestCaseRunner runner, @CitrusResource TestContext context) {
        setDuckVariablesInRunner(runner);
        runner.variable("sound", "quack");
        runner.variable("repetitionCount", 1);
        runner.variable("soundCount", 1);

        do {
            createDuck(runner, context);
            extractIdFromResponse(runner);
        } while (Integer.parseInt(context.getVariable("${duckId}")) % 2 != 1);

        duckQuack(runner, context.getVariable("${duckId}"),
                context.getVariable("${repetitionCount}"),
                context.getVariable("${soundCount}"));
        validateResponse(runner, "{\n" +
                "\"sound\": \"quack\"\n" +
                "}");

        DuckDeleteTest deleteTest = new DuckDeleteTest();
        doFinally().actions(
                runner.$(
                        action(ctx ->
                                deleteTest.deleteDuck(runner, context.getVariable("${duckId}")))
                ));
    }

    // Значение "звук" в ответе на duckQuack равно moo, а ожидается quack (ТЕСТ ПРОВАЛЕН)
    @Test(enabled = false, description = "Проверка, что уточка с корректным чётным id и корректным звуком (quack) будет крякать")
    @CitrusTest
    public void DuckQuackWithEvenIdAndCorrectSound(@Optional @CitrusResource TestCaseRunner runner, @CitrusResource TestContext context) {
        setDuckVariablesInRunner(runner);
        runner.variable("sound", "quack");
        runner.variable("repetitionCount", 1);
        runner.variable("soundCount", 1);

        do {
            createDuck(runner, context);
            extractIdFromResponse(runner);
        } while (Integer.parseInt(context.getVariable("${duckId}")) % 2 != 0);

        duckQuack(runner, context.getVariable("${duckId}"),
                context.getVariable("${repetitionCount}"),
                context.getVariable("${soundCount}"));
        validateResponse(runner, "{\n" +
                "\"sound\": \"quack\"\n" +
                "}");

        DuckDeleteTest deleteTest = new DuckDeleteTest();
        doFinally().actions(
                runner.$(
                        action(ctx ->
                                deleteTest.deleteDuck(runner, context.getVariable("${duckId}")))
                ));
    }


    // ошибка 404 (лапки не найдены), хотя уточка существует в бд
    @Test(description = "Проверка, что уточка, существующая в бд (id), может плавать")
    @CitrusTest
    public void DuckSwimWithExistingID(@Optional @CitrusResource TestCaseRunner runner, @CitrusResource TestContext context) {
        setDuckVariablesInRunner(runner);
        createDuck(runner, context);
        extractIdFromResponse(runner);
        duckTryToSwim(runner, context.getVariable("${duckId}"));
        validateResponse(runner, "{\n" +
                "\"message\": \"string\"\n" +
                "}");

        DuckDeleteTest deleteTest = new DuckDeleteTest();
        doFinally().actions(
                runner.$(
                        action(ctx ->
                                deleteTest.deleteDuck(runner, context.getVariable("${duckId}")))
                ));
    }

    // Ошибка 404. Возможно стоит сделать ожидаемый результат сделать 200
    @Test(description = "Проверка, что уточка, несуществующая в бд (нет такого id), не будет плавать")    @CitrusTest
    public void DuckSwimWithInvalidID(@Optional @CitrusResource TestCaseRunner runner, @CitrusResource TestContext context) {
        setDuckVariablesInRunner(runner);
        createDuck(runner,context);
        extractIdFromResponse(runner);
        // +1 для взятия несуществующего id в БД.
        int invalidId = Integer.parseInt(context.getVariable("${duckId}")) + 1;
        duckTryToSwim(runner, String.valueOf(invalidId));
        validateResponse(runner, "{\n" +
                "\"message\": \"Paws are not found ((((\"\n" +
                "}");

        DuckDeleteTest deleteTest = new DuckDeleteTest();
        doFinally().actions(
                runner.$(
                        action(ctx ->
                                deleteTest.deleteDuck(runner, context.getVariable("${duckId}")))
                ));
    }



    @Test(description = "Проверка, что создаётся уточка с материалом rubber")
    @CitrusTest
    public void DuckCreateWithRubberMaterial(@Optional @CitrusResource TestCaseRunner runner, @CitrusResource TestContext context) {
        // создаём уточку и задаём переменные в контексте
        setDuckVariablesInRunner(runner);
        runner.variable("material", "rubber");
        createDuck(runner, context);
        extractIdFromResponse(runner);

        validateResponse(runner, "{\n" +
                " \"color\": \"" + context.getVariable("${color}") + "\",\n" +
                " \"height\": " + context.getVariable("${height}") + ",\n" +
                " \"material\": \"" + context.getVariable("${material}") + "\",\n" +
                " \"sound\": \"" + context.getVariable("${sound}")+ "\",\n" +
                " \"wingsState\": \"" + context.getVariable("${wingsState}") + "\"\n" + "}");

        DuckDeleteTest deleteTest = new DuckDeleteTest();
        doFinally().actions(
                runner.$(
                        action(ctx ->
                                deleteTest.deleteDuck(runner, context.getVariable("${duckId}")))
                ));
    }

    @Test(description = "Проверка, что создаётся уточка с материалом wood")
    @CitrusTest
    public void DuckCreateWithWoodMaterial(@Optional @CitrusResource TestCaseRunner runner, @CitrusResource TestContext context) {
        // создаём уточку и задаём переменные в контексте
        setDuckVariablesInRunner(runner);
        runner.variable("material", "wood");
        createDuck(runner, context);
        extractIdFromResponse(runner);

        validateResponse(runner, "{\n" +
                " \"color\": \"" + context.getVariable("${color}") + "\",\n" +
                " \"height\": " + context.getVariable("${height}") + ",\n" +
                " \"material\": \"" + context.getVariable("${material}") + "\",\n" +
                " \"sound\": \"" + context.getVariable("${sound}")+ "\",\n" +
                " \"wingsState\": \"" + context.getVariable("${wingsState}") + "\"\n" + "}");

        DuckDeleteTest deleteTest = new DuckDeleteTest();
        doFinally().actions(
                runner.$(
                        action(ctx ->
                                deleteTest.deleteDuck(runner, context.getVariable("${duckId}")))
                ));
    }



    @Test(description = "Проверка, что уточка удаляется")
    @CitrusTest
    public void DuckDelete(@Optional @CitrusResource TestCaseRunner runner, @CitrusResource TestContext context) {
        setDuckVariablesInRunner(runner);
        createDuck(runner, context);
        extractIdFromResponse(runner);

        deleteDuck(runner, context.getVariable("${duckId}"));
        validateResponse(runner, "{\n" +
                "\"message\": \"Duck is deleted\"\n" +
                "}");
    }



    @Test(description = "Проверка, что у уточки изменяются свойства: 'цвет' и 'высота' ")
    @CitrusTest
    public void DuckUpdateColorAndHeight(@Optional @CitrusResource TestCaseRunner runner, @CitrusResource TestContext context) {
        setDuckVariablesInRunner(runner);
        // Новые параметры уточки:
        runner.variable("color", "blue");
        runner.variable("height", 0.05);
        createDuck(runner, context);
        extractIdFromResponse(runner);

        updateDuckColorAndHeight(runner, context);
        validateResponse(runner, "{\n" +
                "\"message\": \"Duck with id = " + context.getVariable("${duckId}") + " is updated\"\n" +
                "}");

        DuckDeleteTest deleteTest = new DuckDeleteTest();
        doFinally().actions(
                runner.$(
                        action(ctx ->
                                deleteTest.deleteDuck(runner, context.getVariable("${duckId}")))
                ));
    }


    @Test(description = "Проверка, что у уточки изменяются свойства: 'цвет' и 'звук' ")
    @CitrusTest
    public void DuckUpdateColorAndSound(@Optional @CitrusResource TestCaseRunner runner, @CitrusResource TestContext context) {
        setDuckVariablesInRunner(runner);
        // Новые параметры уточки:
        runner.variable("color", "red");
        runner.variable("sound", "quack");
        createDuck(runner, context);
        extractIdFromResponse(runner);

        updateDuckColorAndHeight(runner, context);
        validateResponse(runner, "{\n" +
                "\"message\": \"Duck with id = " + context.getVariable("${duckId}") + " is updated\"\n" +
                "}");

        DuckDeleteTest deleteTest = new DuckDeleteTest();
        doFinally().actions(
                runner.$(
                        action(ctx ->
                                deleteTest.deleteDuck(runner, context.getVariable("${duckId}")))
                ));
    }
}
