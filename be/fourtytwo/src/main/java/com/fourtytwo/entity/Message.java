package com.fourtytwo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Message extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_idx")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_idx")
    private User user;

    private String content;
    private Boolean isActive;

    @OneToMany(mappedBy = "message", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Expression> expressions = new ArrayList<>();

    @OneToMany(mappedBy = "message1", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Brush> brushes1 = new ArrayList<>();

    @OneToMany(mappedBy = "message2", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Brush> brushes2 = new ArrayList<>();

    @OneToMany(mappedBy = "message", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Report> reports = new ArrayList<>();
}
