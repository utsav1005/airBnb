package com.bhavsar.airBnb.serviceIimpl;

import com.bhavsar.airBnb.exception.SessionExpiredfException;
import com.bhavsar.airBnb.exception.SessionRevokeException;
import com.bhavsar.airBnb.model.Session;
import com.bhavsar.airBnb.model.User;
import com.bhavsar.airBnb.repository.SessionRepository;
import com.bhavsar.airBnb.service.SessionService;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {
    private final SessionRepository sessionRepository;
    private static final int SESSION_LIMIT = 2;

    @Override
    public Session generateNewSession(User user, String refreshToken) {
        List<Session> userSessions = sessionRepository.findByUserAndRevokedFalse(user);
        if(userSessions.size() >= SESSION_LIMIT){
            userSessions.sort(Comparator.comparing(Session::getLastUsedAt));
            Session lastRecentlyUsedSession = userSessions.getFirst();
            lastRecentlyUsedSession.setRevoked(true);
            sessionRepository.save(lastRecentlyUsedSession);
        }
        Session session = Session.builder()
                .user(user)
                .refreshToken(refreshToken)
                .lastUsedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(7))
                .revoked(false)
                .build();
        return sessionRepository.save(session);
    }

    @Override
    public void validateSession(String refreshToken) {
        Session session = sessionRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new JwtException("Jwt Refresh token is expired"));
        if(session.isRevoked()) {
            throw new SessionRevokeException("Session is revoked ");
        }
        if(session.getExpiresAt().isBefore(LocalDateTime.now())){
            throw new SessionExpiredfException("Session is expired");
        }

    }
}
