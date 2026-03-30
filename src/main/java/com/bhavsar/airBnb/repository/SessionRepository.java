package com.bhavsar.airBnb.repository;


import com.bhavsar.airBnb.model.Session;
import com.bhavsar.airBnb.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session , Long> {
    List<Session> findByUserAndRevokedFalse(User user);
    Optional<Session> findByRefreshToken(String refreshToken);



}
