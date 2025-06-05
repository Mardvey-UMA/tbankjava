package tb.wca.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tb.wca.dto.UserCreateResponseDTO;
import tb.wca.entity.SubscriptionEntity;
import tb.wca.entity.UserEntity;
import tb.wca.exceptions.UserNotFoundException;
import tb.wca.mapper.SubscriptionMapper;
import tb.wca.mapper.UserMapper;
import tb.wca.model.SubscriptionModel;
import tb.wca.repository.UserRepository;
import tb.wca.service.interfaces.UserService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private SubscriptionMapper subscriptionMapper;

    @Override
    public UserCreateResponseDTO createUser(Long telegramId) {
        UserEntity userEntity = userRepository
                .findByTelegramId(telegramId)
                .orElseGet(() -> userRepository.save(new UserEntity(telegramId)));

        return userMapper.entityToResponseDto(userEntity);
    }

    @Override
    public SubscriptionModel getUserSubscription(Long telegramId) {
        UserEntity user = userRepository.findByTelegramId(telegramId)
                .orElseThrow(() -> new UserNotFoundException(telegramId));

        return Optional.ofNullable(user.getSubscription())
                .map(subscriptionMapper::entityToModel)
                .orElse(null);
    }
}
