package com.project.api.metting.repository;

import com.project.api.metting.entity.ChatRooms;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomsRepository extends JpaRepository<ChatRooms, String> {
}
