package com.dizplai.dizplai_tech_test.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Entity
public class Response {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(optional = false)
    private Poll poll;

    private String user;

    @ManyToOne(optional = false)
    private PollOption pollOption;

    private OffsetDateTime responseTime = OffsetDateTime.now();

}
