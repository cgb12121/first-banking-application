package com.backend.bank.repository;

import com.backend.bank.entity.Staff;
import com.backend.bank.entity.StaffAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {

}