package int221.integrated1backend.controllers;

import int221.integrated1backend.exceptions.ItemNotFoundException;
import int221.integrated1backend.exceptions.myErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<myErrorResponse> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
        myErrorResponse errorResponse = new myErrorResponse(HttpStatus.BAD_REQUEST.value(), "Validation error. Check 'errors' field for details.", request.getDescription(false));
        //loop not valid field
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String fieldName = violation.getPropertyPath().toString();
            String errorMessage = violation.getMessage();
            errorResponse.addValidationError(fieldName, errorMessage);
        }
        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<myErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        myErrorResponse errorResponse = new myErrorResponse(HttpStatus.BAD_REQUEST.value(), "Validation error. Check 'errors' field for details.", request.getDescription(false));
        //loop not valid field
        for (int i = 0; i < ex.getErrorCount(); i++) {
            FieldError err = ex.getFieldErrors().get(i);
            errorResponse.addValidationError(err.getField(), err.getDefaultMessage());
        }
        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<myErrorResponse> handleResponseStatusException(
            ResponseStatusException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, request);
    }

    private ResponseEntity<myErrorResponse> buildErrorResponse(
            Exception exception, HttpStatus httpStatus, WebRequest request) {
        return buildErrorResponse(exception, exception.getMessage(), httpStatus, request);
    }

    private ResponseEntity<myErrorResponse> buildErrorResponse(
            Exception exception, String message, HttpStatus httpStatus, WebRequest request) {
        myErrorResponse errorResponse = new myErrorResponse(httpStatus.value(), message, request.getDescription(false)
        );
        return ResponseEntity.status(httpStatus).body(errorResponse);
    }
//    @ExceptionHandler(Exception.class) //exception กลาง
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public ResponseEntity<myErrorResponse> handleAllUncaughtException(
//            Exception exception, WebRequest request) {
//        return buildErrorResponse(exception, "Unknown error occurred", HttpStatus.INTERNAL_SERVER_ERROR, request
//        );
//    }

//    @ExceptionHandler({ItemNotFoundException.class, ResponseStatusException.class, HttpClientErrorException.class})
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    public ResponseEntity<myErrorResponse> handleItemNotFoundException(
//            RuntimeException exception, WebRequest request) {
//        return buildErrorResponse(exception, HttpStatus.NOT_FOUND, request);
//    }
}
