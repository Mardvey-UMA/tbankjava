package tb.wca.service.interfaces;

import tb.wca.dto.UserCreateResponseDTO;
import tb.wca.model.SubscriptionModel;

import java.util.Optional;

public interface UserService {
    UserCreateResponseDTO createUser(Long telegramId);
    SubscriptionModel getUserSubscription(Long telegramId);
}
