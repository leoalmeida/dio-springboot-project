package space.lasf.springboot_project.core.util;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ObjectsValidator Tests")
class ObjectsValidatorTest {

    private ObjectsValidator<TestDto> validator;

    @BeforeEach
    void setUp() {
        validator = new ObjectsValidator<>();
    }

    @Data
    @Builder
    private static class TestDto {
        @NotNull(message = "Name cannot be null")
        private String name;

        @Min(value = 18, message = "Age must be at least 18")
        private Integer age;
    }

    @Test
    @DisplayName("Should not throw exception for a valid object")
    void shouldNotThrowException_whenObjectIsValid() {
        TestDto validDto = TestDto.builder().name("John Doe").age(25).build();
        assertDoesNotThrow(() -> validator.validate(validDto));
    }

    @Test
    @DisplayName("Should throw exception when a @NotNull field is null")
    void shouldThrowException_whenFieldIsNull() {
        TestDto invalidDto = TestDto.builder().name(null).age(25).build();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> validator.validate(invalidDto));
        assertTrue(exception.getMessage().contains("Name cannot be null"));
    }

    @Test
    @DisplayName("Should throw exception when a @Min constraint is violated")
    void shouldThrowException_whenFieldIsBelowMin() {
        TestDto invalidDto = TestDto.builder().name("Jane Doe").age(17).build();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> validator.validate(invalidDto));
        assertTrue(exception.getMessage().contains("Age must be at least 18"));
    }

    @Test
    @DisplayName("Should throw exception with messages for multiple violations")
    void shouldThrowException_withMultipleViolations() {
        TestDto invalidDto = TestDto.builder().name(null).age(16).build();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> validator.validate(invalidDto));
        String message = exception.getMessage();
        assertTrue(message.contains("Name cannot be null"));
        assertTrue(message.contains("Age must be at least 18"));
    }
}