package autotests.payloads;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
@JsonInclude(JsonInclude.Include.NON_NULL)

// Согласно требованиям для duck-service-1.0.0, ожидаемый ответ от properties будет с такими свойствами.
// Используется для создания уточки с набором необходимых полей (5)
// Также данная модель используется для валидации ответа метода properties "/api/duck/action/properties"
public class DuckPropertiesPayload {
    @JsonProperty
    private String color;

    @JsonProperty
    private double height;

    @JsonProperty
    private String material;

    @JsonProperty
    private String sound;

    @JsonProperty
    private DuckWingsState wingsState;
}