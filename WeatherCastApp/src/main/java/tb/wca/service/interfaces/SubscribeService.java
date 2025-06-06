package tb.wca.service.interfaces;

import tb.wca.dto.SubscriptionRequestDTO;
import tb.wca.dto.SubscriptionResponseDTO;

public interface SubscribeService {
    SubscriptionResponseDTO createSubscribe(SubscriptionRequestDTO subscriptionRequestDTO, Long telegramId);
    SubscriptionResponseDTO deleteSubscribe(Long telegramId);
    SubscriptionResponseDTO updateSubscribe(SubscriptionRequestDTO subscriptionRequestDTO, Long telegramId);
}
