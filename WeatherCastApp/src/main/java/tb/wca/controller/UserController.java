package tb.wca.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
}
