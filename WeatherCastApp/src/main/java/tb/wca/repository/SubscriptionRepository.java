package tb.wca.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tb.wca.entity.SubscriptionEntity;
import tb.wca.entity.UserEntity;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, Long> {
    Optional<SubscriptionEntity> findByUser(UserEntity user);
    List<SubscriptionEntity> findByIsActive(boolean b);
}
