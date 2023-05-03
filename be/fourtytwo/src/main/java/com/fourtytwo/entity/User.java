package com.fourtytwo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_idx")
    private Long id;

    private String email;
    private String nickname;
    private String roles;
    private Boolean isActive;
    private String emoji;
    private String color;
    private String appleId;
    private String fcmToken;
    private LocalDateTime fcmTokenExpirationDateTime;

    public List<String> getRoleList() {
        if (this.roles.length() > 0) {
            return Arrays.asList(this.roles.split(","));
        }
        return new ArrayList<>();
    }

    @OneToMany(mappedBy = "user1", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Brush> brushes1 = new ArrayList<>();

    @OneToMany(mappedBy = "user2", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Brush> brushes2 = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Expression> expressions = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Message> messages = new ArrayList<>();

    @OneToMany(mappedBy = "user1", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Report> reports1 = new ArrayList<>();

    @OneToMany(mappedBy = "user2", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Report> reports2 = new ArrayList<>();

    @OneToMany(mappedBy = "user1", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Block> blocks1 = new ArrayList<>();

    @OneToMany(mappedBy = "user2", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Block> blocks2 = new ArrayList<>();
}
