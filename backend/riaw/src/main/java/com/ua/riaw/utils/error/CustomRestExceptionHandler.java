package com.ua.riaw.utils.error;

import com.ua.riaw.utils.error.exceptions.EntityNotFoundException;
import com.ua.riaw.utils.error.exceptions.UnauthorizedAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice(annotations = RestController.class)
public class CustomRestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = EntityNotFoundException.class)
    protected ResponseEntity<?> handleETLNotFound(RuntimeException ex) {
        String error = "ETL procedure not found";
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND);
        apiError.setMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(value = UnauthorizedAccessException.class)
    protected ResponseEntity<?> handleUnauthorizedAccess(RuntimeException ex) {
        ApiError error = new ApiError(HttpStatus.UNAUTHORIZED);
        error.setMessage(ex.getMessage());
        return buildResponseEntity(error);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String error = "Internal error";
        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, error, ex);
        return buildResponseEntity(apiError);
    }

    private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        return ResponseEntity.status(apiError.getStatus()).body(apiError);
    }



}
