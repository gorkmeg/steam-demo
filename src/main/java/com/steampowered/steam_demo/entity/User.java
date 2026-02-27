package com.steampowered.steam_demo.entity;

import com.steampowered.steam_demo.exception.domain.ApiDomainException;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Entity
@Data
@Table(
        name = "users",
        uniqueConstraints = @UniqueConstraint(name = "uk_users_username", columnNames = "username")
)
public class User {
    private static final int DISPLAY_NAME_MAX_LENGTH = 50;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String displayName;
    @Column(nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserType userType = UserType.ROLE_USER;

    public void addBalance(BigDecimal amount) {
        balance = currentBalance().add(amount);
    }

    public void purchase(BigDecimal price) {
        BigDecimal currentBalance = currentBalance();
        if (currentBalance.compareTo(price) < 0) {
            throw new ApiDomainException(HttpStatus.CONFLICT, "Insufficient balance");
        }
        balance = currentBalance.subtract(price);
    }

    public void refund(BigDecimal amount) {
        balance = currentBalance().add(amount);
    }

    public void changeDisplayName(String displayName) {
        String normalizedDisplayName = displayName.trim();
        if (normalizedDisplayName.length() > DISPLAY_NAME_MAX_LENGTH) {
            throw new ApiDomainException(HttpStatus.BAD_REQUEST, "Display name too long");
        }
        this.displayName = normalizedDisplayName;
    }

    private BigDecimal currentBalance() {
        return Objects.requireNonNullElse(balance, BigDecimal.ZERO);
    }
}
