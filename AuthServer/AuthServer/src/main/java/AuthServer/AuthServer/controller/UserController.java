package AuthServer.AuthServer.controller;
import AuthServer.AuthServer.service.LoginService;
import AuthServer.AuthServer.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
public class UserController {

    private final LoginService loginService;
    private final RedisService redisService;

    @Autowired
    public UserController(LoginService loginService, RedisService redisService) {
        this.loginService = loginService;
        this.redisService = redisService;
    }

    @GetMapping("/api/auth/token")
    public ResponseEntity<Map<String, String>> getUserId(@RequestHeader("Authorization") String token) {
        Map<String, String> response = new HashMap<>();
        try {
            String userId = loginService.getId(token, "google");
            redisService.saveUserId("id", userId); // 사용자 ID를 Redis에 저장
            response.put("id", userId);
            return ResponseEntity.ok(response);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED || e.getStatusCode() == HttpStatus.FORBIDDEN) {
                response.put("error", "잘못된 토큰");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            } else {
                response.put("error", "서버 오류 발생");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        }
    }

    @GetMapping("/api/id")
    public ResponseEntity<Set<String>> getAllUserIds() {
        Set<String> userIds = redisService.getAllUserIds("id");
        return ResponseEntity.ok(userIds);
    }

}
