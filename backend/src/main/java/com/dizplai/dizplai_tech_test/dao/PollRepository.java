package com.dizplai.dizplai_tech_test.dao;

import com.dizplai.dizplai_tech_test.model.Poll;
import org.springframework.data.repository.CrudRepository;

public interface PollRepository extends CrudRepository<Poll, Integer> {

}
