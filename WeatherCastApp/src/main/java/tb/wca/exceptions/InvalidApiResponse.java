package tb.wca.exceptions;

import lombok.Getter;
import tb.wca.exceptions.enums.BusinessErrorCodes;
@Getter
public class InvalidApiResponse extends BusinessException {

    public InvalidApiResponse() {
        super(BusinessErrorCodes.INVALID_API_RESPONSE);
    }

    public InvalidApiResponse(String additionalMessage) {
        super(BusinessErrorCodes.INVALID_API_RESPONSE, additionalMessage);
    }
}
