package space.lasf.springboot_project.core.exception;

import jakarta.annotation.Resource;
import jakarta.validation.ConstraintViolationException;
import space.lasf.springboot_project.core.util.ResponseError;

import org.springframework.cglib.proxy.UndeclaredThrowableException;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.Instant;
import java.util.Locale;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    
    @Resource
    private MessageSource messageSource;
    
    private HttpHeaders headers(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private ResponseError responseError(String message,HttpStatus statusCode){
        ResponseError responseError = new ResponseError();
        responseError.setStatus("error");
        responseError.setError(message);
        responseError.setStatusCode(statusCode.value());
        return responseError;
    }

    @ExceptionHandler(Exception.class)
    private ResponseEntity<Object> handleGeneral(Exception e, WebRequest request) {
        if (e.getClass().isAssignableFrom(UndeclaredThrowableException.class)) {
            UndeclaredThrowableException exception = (UndeclaredThrowableException) e;
            return handleBusinessException((BusinessException) exception.getUndeclaredThrowable(), request);
        } else {
            String message = messageSource.getMessage("error.server", new Object[]{e.getMessage()}, Locale.getDefault());
            ResponseError error = responseError(message,HttpStatus.INTERNAL_SERVER_ERROR);
            return handleExceptionInternal(e, error, headers(), HttpStatus.INTERNAL_SERVER_ERROR, request);
        }
    }

    @ExceptionHandler({BusinessException.class})
    private ResponseEntity<Object> handleBusinessException(BusinessException e, WebRequest request) {
        ResponseError error = responseError(e.getMessage(),HttpStatus.UNPROCESSABLE_ENTITY);
        return handleExceptionInternal(e, error, headers(), HttpStatus.UNPROCESSABLE_ENTITY, request);
    }

    @ExceptionHandler({NoSuchElementException.class})
    private ResponseEntity<Object> handleNotFoundException(NoSuchElementException e, WebRequest request) {
        ResponseError error = responseError(e.getMessage(),HttpStatus.NOT_FOUND);
        return handleExceptionInternal(e, error, headers(), HttpStatus.NOT_FOUND, request);
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException e, WebRequest request) {
        ResponseError error = responseError(e.getMessage(),HttpStatus.BAD_REQUEST);
        return handleExceptionInternal(e, error, headers(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(ConstraintViolationException e, WebRequest request) {
        ResponseError error = responseError(e.getMessage(),HttpStatus.BAD_REQUEST);
        return handleExceptionInternal(e, error, headers(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = {ControllerException.class})
    public ResponseEntity<ProblemDetail> handleIllegalStateException(ControllerException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage()); 
        problemDetail.setTitle("Controller Exception");
        problemDetail.setDetail(ex.getErrorMessage());
        problemDetail.setType(URI.create("http://localhost:8000/errors/500"));
        problemDetail.setProperty("isBusinessError", "true");
        problemDetail.setProperty("timestamp", Instant.now());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }
}
