package com.usermanager.manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.usermanager.manager.model.verification.VerificationToken;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long>{

}
