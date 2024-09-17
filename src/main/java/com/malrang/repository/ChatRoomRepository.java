package com.malrang.repository;

import com.malrang.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    List<ChatRoom> findByRoomNameContainingAndRoomLanguageContainingAndRoomLevelContaining(String roomName, String roomLanguage, String roomLevel);

    Optional<ChatRoom> findByRoomId(String roomId);
    void deleteByRoomId(String roomId);
}
