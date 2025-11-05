package com.voting.system.repository;

import com.voting.system.model.VoteRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface VoteRecordRepository extends JpaRepository<VoteRecord, Long> {
    Optional<VoteRecord> findByUserIdAndRoomId(Long userId, String roomId);
    boolean existsByUserIdAndRoomId(Long userId, String roomId);
}
