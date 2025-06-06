package tb.wca.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tb.wca.dto.SubscriptionInfo;
import tb.wca.dto.UserCreateResponseDTO;
import tb.wca.service.interfaces.UserService;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserCreateResponseDTO> createUser(@RequestHeader("X-Telegram-Id") Long telegramId) {
        return ResponseEntity.ok(userService.createUser(telegramId));
    }

    @GetMapping
    public ResponseEntity<SubscriptionInfo> getInfo(@RequestHeader("X-Telegram-Id") Long telegramId){
        return ResponseEntity.ok(userService.getSubInfo(telegramId));
    }
}
