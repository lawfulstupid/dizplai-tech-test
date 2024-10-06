package com.dizplai.dizplai_tech_test.dao;

import com.dizplai.dizplai_tech_test.model.Poll;
import com.dizplai.dizplai_tech_test.model.enums.PollStatus;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface PollRepository extends CrudRepository<Poll, Integer> {

    Optional<Poll> findFirstByStatus(PollStatus status);
    List<Poll> findAllByStatus(PollStatus status);

}
