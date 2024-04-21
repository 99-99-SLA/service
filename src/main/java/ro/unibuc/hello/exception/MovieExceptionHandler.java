package ro.unibuc.hello.exception;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

@ControllerAdvice
public class MovieExceptionHandler extends ResponseEntityExceptionHandler {
    private MeterRegistry meterRegistry;

    public MovieExceptionHandler(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> resourceNotFoundException(EntityNotFoundException ex, WebRequest request) {
        final ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), request.getDescription(false));
        updateMeterRegistry(ex, errorDetails.getDetails().substring(4));
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> illegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        final ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), request.getDescription(false));
        updateMeterRegistry(ex, errorDetails.getDetails().substring(4));
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> globalExceptionHandler(Exception ex, WebRequest request) {
        final ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), request.getDescription(false));
        updateMeterRegistry(ex, errorDetails.getDetails().substring(4));
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void updateMeterRegistry(Exception e, String endpoint) {
        Counter.builder("errors")
                .tag("endpoint", endpoint)
                .tag("exception", e.getClass().getSimpleName())
                .register(meterRegistry)
                .increment();
    }
}