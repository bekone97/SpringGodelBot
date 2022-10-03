package com.godeltech.springgodelbot.model.repository;

import com.godeltech.springgodelbot.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByIdAndUserName(Long id, String username);

}
