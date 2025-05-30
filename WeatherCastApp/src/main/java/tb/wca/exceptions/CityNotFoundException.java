package tb.wca.exceptions;

import lombok.Getter;
import tb.wca.exceptions.enums.BusinessErrorCodes;
@Getter
public class CityNotFoundException extends BusinessException{

    public CityNotFoundException() {
        super(BusinessErrorCodes.CITY_NOT_FOUND);
    }

    public CityNotFoundException(String additionalMessage) {
        super(BusinessErrorCodes.CITY_NOT_FOUND, additionalMessage);
    }
}
