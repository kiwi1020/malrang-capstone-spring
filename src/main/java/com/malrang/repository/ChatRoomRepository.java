package com.malrang.repository;

import com.malrang.entity.Article;
import com.malrang.entity.ChatRoom;
import com.malrang.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    List<ChatRoom> findByRoomNameContainingAndRoomLanguageContainingAndRoomLevelContaining(String roomName, String roomLanguage, String roomLevel);
}
