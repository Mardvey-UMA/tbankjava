package tb.wca.service.notification;

import jakarta.annotation.PreDestroy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Service
public class NotificationTaskScheduler {
    private final ThreadPoolTaskScheduler taskScheduler;
    private final Map<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    public NotificationTaskScheduler() {
        this.taskScheduler = new ThreadPoolTaskScheduler();
        this.taskScheduler.setPoolSize(10);
        this.taskScheduler.setThreadNamePrefix("notification-task-");
        this.taskScheduler.initialize();
    }

    public void scheduleTask(Long subscriptionId, ZonedDateTime triggerTime, Runnable task) {
        cancelTask(subscriptionId);

        long delay = calculateDelayMillis(triggerTime);

        ScheduledFuture<?> future = taskScheduler.schedule(task,
                Instant.now().plusMillis(delay));

        scheduledTasks.put(subscriptionId, future);
    }

    public void cancelTask(Long subscriptionId) {
        ScheduledFuture<?> future = scheduledTasks.get(subscriptionId);
        if (future != null) {
            future.cancel(true);
            scheduledTasks.remove(subscriptionId);
        }
    }

    private long calculateDelayMillis(ZonedDateTime triggerTime) {
        return Duration.between(ZonedDateTime.now(), triggerTime).toMillis();
    }

    @PreDestroy
    public void shutdown() {
        taskScheduler.shutdown();
    }
}
