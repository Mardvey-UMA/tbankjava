package tb.wca.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tb.wca.dto.SubscriptionInfo;
import tb.wca.dto.UserCreateResponseDTO;
import tb.wca.entity.SubscriptionEntity;
import tb.wca.entity.UserEntity;
import tb.wca.mapper.UserMapper;
import tb.wca.repository.UserRepository;
import tb.wca.service.interfaces.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;

    @Override
    public UserCreateResponseDTO createUser(Long telegramId) {
        UserEntity userEntity = userRepository
                .findByTelegramId(telegramId)
                .orElseGet(() -> userRepository.save(new UserEntity(telegramId)));

        return userMapper.entityToResponseDto(userEntity);
    }

    @Override
    public SubscriptionInfo getSubInfo(Long telegramId) {
        UserEntity userEntity = userRepository
                .findByTelegramId(telegramId)
                .orElseGet(() -> userRepository.save(new UserEntity(telegramId)));
        SubscriptionEntity se = userEntity.getSubscription();
        return SubscriptionInfo.builder()
                .cityName(se.getCity().getName())
                .notificationTime(se.getNotificationTime())
                .timeZone(se.getTimeZone())
                .build();
    }
}
