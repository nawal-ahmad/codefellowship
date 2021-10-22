package com.codefellowship.repositories;

import com.codefellowship.models.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicationUserRepository extends JpaRepository<ApplicationUser, Long> {
    ApplicationUser findApplicationUserByUsername(String username);
    ApplicationUser findApplicationUserById(Long id);
}
