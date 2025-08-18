package com.metaverse.aurai_adra.repository;

import com.metaverse.aurai_adra.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;



public interface UserRepository extends JpaRepository<User, String> {
}