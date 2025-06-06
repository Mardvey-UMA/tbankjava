package tb.wca.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tb.wca.entity.UserEntity;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByTelegramId(Long telegramId);
}
