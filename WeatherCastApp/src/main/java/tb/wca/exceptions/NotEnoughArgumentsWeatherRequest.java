package tb.wca.exceptions;

import lombok.Getter;
import tb.wca.exceptions.enums.BusinessErrorCodes;
@Getter
public class NotEnoughArgumentsWeatherRequest extends BusinessException {

    public NotEnoughArgumentsWeatherRequest() {
        super(BusinessErrorCodes.NOT_ENOUGH_ARGUMENTS);
    }

    public NotEnoughArgumentsWeatherRequest(String additionalMessage) {
        super(BusinessErrorCodes.NOT_ENOUGH_ARGUMENTS, additionalMessage);
    }
}
