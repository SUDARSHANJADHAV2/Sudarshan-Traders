package com.sudarshan.trader.repository;

import com.sudarshan.trader.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByGstNumber(String gstNumber);

    List<User> findByRoleAndVerified(String role, Boolean verified);

    List<User> findByRole(String role);

    Boolean existsByEmail(String email);

    Boolean existsByGstNumber(String gstNumber);
}
