package com.dizplai.dizplai_tech_test.controller;

import com.dizplai.dizplai_tech_test.dao.PollRepository;
import com.dizplai.dizplai_tech_test.model.PollOption;
import com.dizplai.dizplai_tech_test.model.Poll;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping(path = "/poll")
@RequiredArgsConstructor
public class PollController {

    private final PollRepository pollRepository;

    @PostMapping("/create")
    public @ResponseBody Poll createPoll(@RequestBody CreatePollRequestBody body) {
        // Convert request body to model
        final Poll poll = new Poll();
        poll.setName(body.name());
        poll.setQuestion(body.question());
        body.options().forEach(optionText -> {
            final PollOption pollOption = new PollOption();
            pollOption.setDescription(optionText);
            pollOption.setPoll(poll);
            poll.getPollOptions().add(pollOption);
        });
        return pollRepository.save(poll);
    }

    private record CreatePollRequestBody(
            String name,
            String question,
            List<String> options
    ) {}

}
