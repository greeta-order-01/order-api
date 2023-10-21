package net.greeta.order.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.greeta.order.mapper.UserMapper;
import net.greeta.order.model.User;
import net.greeta.order.rest.dto.CreateOrderRequest;
import net.greeta.order.rest.dto.CreateUserRequest;
import net.greeta.order.rest.dto.UserDto;
import net.greeta.order.rest.dto.UserExistsDto;
import net.greeta.order.security.WebSecurityConfig;
import net.greeta.order.service.UserService;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/me")
    public UserDto getCurrentUser(JwtAuthenticationToken token) {
        return userMapper.toUserDto(userService.validateAndGetUserByUsername(token.getName()));
    }

    @GetMapping("/meExists")
    public UserExistsDto isCurrentUserExists(JwtAuthenticationToken token) {
        return new UserExistsDto(userService.hasUserWithUsername(token.getName()));
    }

    @PostMapping("/me")
    public UserDto saveUser(@Valid @RequestBody CreateUserRequest createUserRequest, JwtAuthenticationToken token) {
        String username = token.getName();
        return userMapper.toUserDto(userService.saveUser(new User(username, createUserRequest.getName(),
                createUserRequest.getEmail(), WebSecurityConfig.ORDER_USER)));
    }

    @GetMapping
    public List<UserDto> getUsers() {
        return userService.getUsers().stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{username}")
    public UserDto getUser(@PathVariable String username) {
        return userMapper.toUserDto(userService.validateAndGetUserByUsername(username));
    }

    @DeleteMapping("/{username}")
    public UserDto deleteUser(@PathVariable String username) {
        User user = userService.validateAndGetUserByUsername(username);
        userService.deleteUser(user);
        return userMapper.toUserDto(user);
    }
}
