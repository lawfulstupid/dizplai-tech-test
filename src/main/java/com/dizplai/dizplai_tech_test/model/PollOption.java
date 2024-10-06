package com.dizplai.dizplai_tech_test.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class PollOption {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;

    @JsonIgnore
    @ManyToOne(optional = false)
    private Poll poll;

    private String description;

    @JsonIgnore
    @OneToMany(mappedBy = "pollOption", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Response> responses = new ArrayList<>();


    /* Conditional results serialisation logic */

    public void setCurrentUser(String userId) {
        if (userId == null) return;
        userSelection = responses.stream().anyMatch(response -> response.getUser().equals(userId));
    }

    private transient boolean userSelection = false;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("votes")
    public Integer getVotes() {
        return poll.isUserComplete() ? responses.size() : null;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("votesPercentage")
    public Double getVotesPercentage() {
        return poll.isUserComplete() ? 100d * getVotes() / poll.getTotalVotes() : null;
    }

}
