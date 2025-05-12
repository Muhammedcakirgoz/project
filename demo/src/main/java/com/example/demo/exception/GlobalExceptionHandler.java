package com.example.demo.exception;

import com.example.demo.dto.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;


@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // 1) Validation hataları
   // @Override   buna sonra bak
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            org.springframework.web.bind.MethodArgumentNotValidException ex,
            org.springframework.http.HttpHeaders headers,
            org.springframework.http.HttpStatus status,
            org.springframework.web.context.request.WebRequest request
    ) {
        List<String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(org.springframework.validation.FieldError::getDefaultMessage)
                .collect(java.util.stream.Collectors.toList());

        com.example.demo.dto.ErrorResponse body = new com.example.demo.dto.ErrorResponse(
                java.time.Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                "Validation failed",
                request.getDescription(false).replace("uri=", ""),
                details
        );

        return new ResponseEntity<>(body, headers, status);
    }


//    // 2) JSON parse hataları
//    @ExceptionHandler(HttpMessageNotReadableException.class)
//    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
//            HttpMessageNotReadableException ex,
//            WebRequest request) {
//
//        HttpStatus status = HttpStatus.BAD_REQUEST;
//        ErrorResponse body = new ErrorResponse(
//                Instant.now(),
//                status.value(),
//                status.getReasonPhrase(),
//                ex.getMostSpecificCause().getMessage(),        // message
//                request.getDescription(false).replace("uri=", ""),
//                List.of(ex.getMostSpecificCause().getMessage())// details
//        );
//        return new ResponseEntity<>(body, status);
//    }

    // 3) Not found
    @ExceptionHandler({NoSuchElementException.class, EntityNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFound(
            RuntimeException ex,
            WebRequest request) {

        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorResponse body = new ErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),  // message
                request.getDescription(false).replace("uri=", ""),
                List.of(ex.getMessage())
        );
        return new ResponseEntity<>(body, status);
    }

    // 4) Access denied
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex,
            WebRequest request) {

        HttpStatus status = HttpStatus.FORBIDDEN;
        ErrorResponse body = new ErrorResponse(
                Instant.now(),                                    // 1
                status.value(),                                   // 2
                status.getReasonPhrase(),                         // 3
                ex.getMessage(),                                  // 4 → message, örn. "Access is denied"
                request.getDescription(false).replace("uri=", ""),// 5 → path
                List.of("You do not have permission to access this resource") // 6 → details
        );
        return new ResponseEntity<>(body, status);
    }


    // 5) Diğer tüm hatalar
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(
            Exception ex,
            WebRequest request) {

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorResponse body = new ErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),  // message
                request.getDescription(false).replace("uri=", ""),
                List.of(ex.getMessage())
        );
        return new ResponseEntity<>(body, status);
    }
}
