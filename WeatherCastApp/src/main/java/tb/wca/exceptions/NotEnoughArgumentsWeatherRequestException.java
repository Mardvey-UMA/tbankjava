package tb.wca.exceptions;

import lombok.Getter;
import tb.wca.exceptions.enums.BusinessErrorCodes;
@Getter
public class NotEnoughArgumentsWeatherRequestException extends BusinessException {

    public NotEnoughArgumentsWeatherRequestException() {
        super(BusinessErrorCodes.NOT_ENOUGH_ARGUMENTS);
    }

    public NotEnoughArgumentsWeatherRequestException(String additionalMessage) {
        super(BusinessErrorCodes.NOT_ENOUGH_ARGUMENTS, additionalMessage);
    }
}

