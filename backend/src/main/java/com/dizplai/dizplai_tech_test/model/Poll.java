package com.dizplai.dizplai_tech_test.model;

import com.dizplai.dizplai_tech_test.model.enums.PollStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Poll {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    private String name;

    private String question;

    @OneToMany(mappedBy = "poll", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PollOption> pollOptions = new ArrayList<>();

    @OneToMany(mappedBy = "poll", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Response> responses = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private PollStatus status = PollStatus.ACTIVE;

    private OffsetDateTime createdAt = OffsetDateTime.now();

}
