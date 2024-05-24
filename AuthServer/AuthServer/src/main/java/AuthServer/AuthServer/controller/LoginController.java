package AuthServer.AuthServer.controller;

import AuthServer.AuthServer.service.LoginService;
import AuthServer.AuthServer.service.RedisService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/api", produces = "application/json")
@Import(RedisService.class)
public class LoginController {
    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @GetMapping("/code/google")
    public void googleLogin(@RequestParam String code) {
        System.out.println("인가 코드 : "+code);
    }

    @PostMapping("/auth/code/google")
    public ResponseEntity<Map<String, String>> googleAuthorizationCode(@RequestBody Map<String, String> requestBody) {
        String code = requestBody.get("code");
        Map<String, String> response = new HashMap<>();
        try {
            String accessToken = loginService.getAccessToken(code, "google");
            response.put("access_token", accessToken);
            return ResponseEntity.ok(response);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                response.put("error", "잘못된 인가 코드");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            } else {
                response.put("error", "서버 오류 발생");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        }
    }
}
