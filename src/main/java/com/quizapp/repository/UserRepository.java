package com.quizapp.repository;

import com.quizapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // ✅ FIXED: Correct query syntax
    @Query("SELECT u FROM User u WHERE u.username = :username")
    Optional<User> findByUsername(@Param("username") String username);

    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.username = :username")
    boolean existsByUsername(@Param("username") String username);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.email = :email")
    boolean existsByEmail(@Param("email") String email);

    @Query("SELECT u FROM User u WHERE u.role = :role")
    List<User> findByRole(@Param("role") com.quizapp.entity.Role role);

    @Query("SELECT u FROM User u WHERE u.enabled = true")
    List<User> findByEnabledTrue();

    @Query("SELECT u FROM User u WHERE u.accountLocked = true")
    List<User> findByAccountLockedTrue();

    @Query("SELECT u FROM User u WHERE u.lockTime < :expiryTime AND u.accountLocked = true")
    List<User> findExpiredLockedAccounts(@Param("expiryTime") LocalDateTime expiryTime);

    @Modifying
    @Query("UPDATE User u SET u.loginAttempts = 0, u.accountLocked = false, u.lockTime = null WHERE u.lockTime < :expiryTime AND u.accountLocked = true")
    void unlockExpiredAccounts(@Param("expiryTime") LocalDateTime expiryTime);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = 'PARTICIPANT' AND u.enabled = true")
    Long countActiveParticipants();

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = 'ADMIN' AND u.enabled = true")
    Long countActiveAdmins();
}