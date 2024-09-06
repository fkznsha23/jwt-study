package com.allclear.jwtstudy.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.allclear.jwtstudy.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {

    User findByUsername(String username);

}
