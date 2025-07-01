package space.lasf.springboot_project.core.exception;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ControllerException extends Exception {

    private final String errorMessage;

    public ControllerException(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}