package net.greeta.order.mapper;

import net.greeta.order.model.User;
import net.greeta.order.rest.dto.UserDto;

public interface UserMapper {

    UserDto toUserDto(User user);
}