package com.example.telegramleadbot.repository;

import com.example.telegramleadbot.entity.LeadEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeadRepository extends JpaRepository<LeadEntity, Long> {

    List<LeadEntity> findTop5ByOrderByCreatedAtDesc();

    List<LeadEntity> findByOrderByCreatedAtDesc(Pageable pageable);

}
