package tb.wca.dto;

import java.time.LocalDateTime;

public record UserCreateResponseDTO(
    Long telegramId,
    LocalDateTime created_at
) { }
