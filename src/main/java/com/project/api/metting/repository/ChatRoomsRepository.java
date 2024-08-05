package com.project.api.metting.repository;

import com.project.api.metting.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomsRepository extends JpaRepository<ChatRoom, String> {


}
