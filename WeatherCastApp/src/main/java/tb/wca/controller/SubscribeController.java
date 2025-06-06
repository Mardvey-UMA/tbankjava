package tb.wca.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tb.wca.dto.SubscriptionRequestDTO;
import tb.wca.dto.SubscriptionResponseDTO;
import tb.wca.service.interfaces.SubscribeService;

@RestController
@RequestMapping("api/subscriptions")
@RequiredArgsConstructor
public class SubscribeController {

    private final SubscribeService subscribeService;

    @PostMapping
    public ResponseEntity<SubscriptionResponseDTO> createSubscription(
            @RequestHeader("X-Telegram-Id") Long telegramId,
            @Valid @RequestBody SubscriptionRequestDTO request) {
        return ResponseEntity.ok(subscribeService.createSubscribe(request, telegramId));
    }

    @PutMapping
    public ResponseEntity<SubscriptionResponseDTO> updateSubscription(
            @RequestHeader("X-Telegram-Id") Long telegramId,
            @RequestBody SubscriptionRequestDTO request) {
        return ResponseEntity.ok(subscribeService.updateSubscribe(request, telegramId));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteSubscription(
            @Valid @RequestHeader("X-Telegram-Id") Long telegramId) {
        subscribeService.deleteSubscribe(telegramId);
        return ResponseEntity.noContent().build();
    }
}
