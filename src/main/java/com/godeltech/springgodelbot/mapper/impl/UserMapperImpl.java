package com.godeltech.springgodelbot.mapper.impl;

import com.godeltech.springgodelbot.mapper.UserMapper;
import com.godeltech.springgodelbot.model.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.User;

@Component
@RequiredArgsConstructor
public class UserMapperImpl implements UserMapper {

    private final ModelMapper modelMapper;

    @Override
    public User mapToUser(UserEntity userEntity) {
        return modelMapper.map(userEntity, User.class);
    }

    @Override
    public UserEntity mapToUserEntity(User user, boolean isValid) {
        UserEntity userEntity= modelMapper.map(user, UserEntity.class);
        userEntity.setValid(isValid);
        return userEntity;
    }

}
