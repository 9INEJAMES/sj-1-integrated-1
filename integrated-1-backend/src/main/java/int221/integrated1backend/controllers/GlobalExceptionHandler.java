package int221.integrated1backend.controllers;

import int221.integrated1backend.exceptions.myErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.web.context.request.WebRequest;

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
}
