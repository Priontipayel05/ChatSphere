package com.chatsphere.repository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.chatsphere.model.ChatRoom;
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    List<ChatRoom> findAllByOrderByNameAsc();
    Optional<ChatRoom> findByNameKey(String nameKey);
    boolean existsByNameKey(String nameKey);
}
