package tb.wca.service.interfaces;

import tb.wca.dto.UserCreateResponseDTO;

public interface UserService {
    UserCreateResponseDTO createUser(Long telegramId);
}
