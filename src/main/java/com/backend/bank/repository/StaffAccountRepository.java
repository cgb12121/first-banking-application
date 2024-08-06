package com.backend.bank.repository;

import com.backend.bank.entity.StaffAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffAccountRepository extends JpaRepository<StaffAccount, Long> {
}