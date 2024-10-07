package com.dizplai.dizplai_tech_test.dao;

import com.dizplai.dizplai_tech_test.model.Response;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ResponseRepository extends CrudRepository<Response, Integer> {

    @Query("FROM Response r " +
            "WHERE r.poll.id = :pollId " +
            "AND (r.pollOption.id = :optionId OR :optionId is null) " +
            "ORDER BY r.responseTime ASC")
    List<Response> getResponsesForPoll(Integer pollId, Integer optionId);

}
