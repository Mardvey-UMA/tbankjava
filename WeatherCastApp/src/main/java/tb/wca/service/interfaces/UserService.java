package tb.wca.service.interfaces;

import tb.wca.dto.SubscriptionInfo;
import tb.wca.dto.UserCreateResponseDTO;

public interface UserService {
    UserCreateResponseDTO createUser(Long telegramId);
    SubscriptionInfo getSubInfo(Long telegramId);
}
