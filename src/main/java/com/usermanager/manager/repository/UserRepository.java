package com.usermanager.manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.usermanager.manager.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}
