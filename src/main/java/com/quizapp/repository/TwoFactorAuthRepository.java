package com.quizapp.repository;

import com.quizapp.entity.TwoFactorAuth;
import com.quizapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TwoFactorAuthRepository extends JpaRepository<TwoFactorAuth, Long> {

    Optional<TwoFactorAuth> findByUser(User user);

    Optional<TwoFactorAuth> findByUserId(Long userId);

    boolean existsByUserAndEnabledTrue(User user);

    @Modifying
    @Query("DELETE FROM TwoFactorAuth t WHERE t.user = :user")
    void deleteByUser(@Param("user") User user);

    @Query("SELECT COUNT(t) > 0 FROM TwoFactorAuth t WHERE t.user.id = :userId AND t.enabled = true")
    boolean is2FAEnabledForUser(@Param("userId") Long userId);
}