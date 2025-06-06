package tb.wca.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExceptionResponse {
    private String errorType;
    private String description;
    private String message;
    private Integer code;

    public ExceptionResponse(String message, int status) {
        this.message = message;
        this.code = status;
    }
}
