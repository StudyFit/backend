package com.farmers.studyfit.domain.member.repository;

import com.farmers.studyfit.domain.member.entity.Member;
import com.farmers.studyfit.domain.member.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByJti(String jti);
    void deleteAllByMember(Member member);
}
