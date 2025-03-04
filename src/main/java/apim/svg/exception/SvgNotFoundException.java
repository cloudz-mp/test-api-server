package apim.svg.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class SvgNotFoundException extends RuntimeException {
    public SvgNotFoundException(String message) {
        super(message);
    }
} 