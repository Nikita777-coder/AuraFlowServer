package app.advice;

import io.netty.handler.timeout.ReadTimeoutException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class ControllerAdvices {
    @ExceptionHandler({IllegalArgumentException.class, NullPointerException.class, ConstraintViolationException.class, UsernameNotFoundException.class})
    @ResponseBody
    private  ResponseEntity<String> handleBAD_REQUEST(RuntimeException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({IllegalStateException.class, ReadTimeoutException.class})
    private @ResponseBody ResponseEntity<String> handleINTERNAL_SERVER_ERROR(RuntimeException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({AccessDeniedException.class})
    private @ResponseBody ResponseEntity<String> handleFORBIDDEN(RuntimeException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }
}
