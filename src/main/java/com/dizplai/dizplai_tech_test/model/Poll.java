package com.dizplai.dizplai_tech_test.model;

import com.dizplai.dizplai_tech_test.model.enums.PollStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    private List<PollOption> options = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "poll", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Response> responses = new ArrayList<>();

    @JsonIgnore
    @Enumerated(EnumType.STRING)
    private PollStatus status = PollStatus.PENDING;

    @JsonIgnore
    private OffsetDateTime createdAt = OffsetDateTime.now();

    /* Conditional results serialisation logic */

    public void setCurrentUser(String userId) {
        if (userId == null) return;
        options.forEach(option -> option.setCurrentUser(userId));
        userComplete = options.stream().anyMatch(PollOption::isUserSelection);
    }

    // Used for deciding whether to serialise results
    private transient boolean userComplete = false;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("totalVotes")
    public Integer getTotalVotes() {
        return userComplete ? responses.size() : null;
    }

}
