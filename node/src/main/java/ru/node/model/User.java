package ru.node.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String firstName;
    private String lastName;
    private String userName;
    private LocalDateTime date;
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<ExchangeUser> exchangeUserList;
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<PaymentSystemUser> paymentSystemUserList;
    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE)
    private LimitUser limitUser;
    public User(Long id) {
        this.id = id;
    }
}
