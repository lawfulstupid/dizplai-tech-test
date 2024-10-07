package com.dizplai.dizplai_tech_test.model;

import com.dizplai.dizplai_tech_test.model.enums.PollStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Poll {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication != null && authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ADMIN"));
        if (userId == null && !isAdmin) return;
        options.forEach(option -> option.setCurrentUser(userId));
        userComplete = options.stream().anyMatch(PollOption::isUserSelection);
        showResults = userComplete || isAdmin;
    }

    // Used for deciding whether to serialise results
    private transient boolean userComplete = false;

    @JsonIgnore
    private transient boolean showResults = false;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("totalVotes")
    public Integer getTotalVotes() {
        return isShowResults() ? responses.size() : null;
    }

}
