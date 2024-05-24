package AuthServer.AuthServer.service;

import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class RedisService {

    private final StringRedisTemplate redisTemplate;

    public RedisService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveUserId(String key, String userId) {
        SetOperations<String, String> setOperations = redisTemplate.opsForSet();
        setOperations.add(key, userId);
    }

    public Set<String> getAllUserIds(String key) {
        SetOperations<String, String> setOperations = redisTemplate.opsForSet();
        return setOperations.members(key);
    }

}