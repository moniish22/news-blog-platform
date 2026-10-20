package com.newsblog.repository;

import com.newsblog.model.Newsletter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NewsletterRepository extends JpaRepository<Newsletter, Long> {
    List<Newsletter> findTop10ByOrderByIdDesc();
}
