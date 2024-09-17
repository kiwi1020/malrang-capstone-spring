package com.malrang.repository;

import com.malrang.entity.ChatRoom;
import com.malrang.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByChatRoom(ChatRoom chatRoom);
}