package cl.duoc.backend_inventario.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException e) {

                var fieldError = e.getBindingResult().getFieldError();

                String message = fieldError != null
                                ? fieldError.getDefaultMessage()
                                : "Error de validación";

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(Map.of(
                                                "error", "VALIDATION_ERROR",
                                                "message", message));
        }

        @ExceptionHandler(RecursoNoEncontradoException.class)
        public ResponseEntity<Map<String, String>> handleNotFound(RecursoNoEncontradoException e) {

                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(Map.of(
                                                "error", "NOT_FOUND",
                                                "message", e.getMessage()));
        }

        @ExceptionHandler(EstadoInvalidoException.class)
        public ResponseEntity<Map<String, String>> handleBusiness(EstadoInvalidoException e) {

                return ResponseEntity.status(HttpStatus.CONFLICT)
                                .body(Map.of(
                                                "error", "BUSINESS_ERROR",
                                                "message", e.getMessage()));
        }

        @ExceptionHandler(ServicioNoDisponibleException.class)
        public ResponseEntity<Map<String, String>> handleService(ServicioNoDisponibleException e) {

                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                                .body(Map.of(
                                                "error", "SERVICE_UNAVAILABLE",
                                                "message", e.getMessage()));
        }

        @ExceptionHandler(RuntimeException.class)
        public ResponseEntity<Map<String, String>> handleRuntime(RuntimeException e) {

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(Map.of(
                                                "error", "BAD_REQUEST",
                                                "message", e.getMessage()));
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<Map<String, String>> handleGeneric(Exception e) {

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(Map.of(
                                                "error", "INTERNAL_ERROR",
                                                "message", "Error inesperado en el sistema"));
        }
}
