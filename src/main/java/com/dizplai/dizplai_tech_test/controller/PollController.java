package com.dizplai.dizplai_tech_test.controller;

import com.dizplai.dizplai_tech_test.dao.PollRepository;
import com.dizplai.dizplai_tech_test.dao.ResponseRepository;
import com.dizplai.dizplai_tech_test.model.PollOption;
import com.dizplai.dizplai_tech_test.model.Poll;
import com.dizplai.dizplai_tech_test.model.Response;
import com.dizplai.dizplai_tech_test.model.enums.PollStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Controller
@RequestMapping(path = "/poll")
@RequiredArgsConstructor
public class PollController {

    private final PollRepository pollRepo;
    private final ResponseRepository responseRepo;

    // Gets a poll
    @Secured("ADMIN")
    @GetMapping("/{id}")
    public @ResponseBody Poll getPollById(@PathVariable Integer id, @CookieValue(name = CookieController.USER_ID_COOKIE, required = false) String userId) {
        return pollRepo.findById(id)
                .map(poll -> returnPoll(poll, userId))
                .orElseThrow(() -> new IllegalArgumentException("Poll not found"));
    }

    // Returns active poll
    @GetMapping
    public @ResponseBody Poll getActivePoll(@CookieValue(name = CookieController.USER_ID_COOKIE, required = false) String userId) {
        return pollRepo.findFirstByStatus(PollStatus.ACTIVE)
                .map(poll -> returnPoll(poll, userId))
                .orElseThrow(() -> new IllegalStateException("No active polls"));
    }

    private Poll returnPoll(Poll poll, String userId) {
        poll.setCurrentUser(userId);
        return poll;
    }

    // Creates a new poll
    @Secured("ADMIN")
    @PostMapping
    public @ResponseBody Poll createPoll(@RequestBody CreatePollRequestBody body, @RequestParam(name = "activate", required = false, defaultValue = "false") boolean activate) {
        // Validation
        if (body.name() == null || body.name().isEmpty()) {
            throw new IllegalArgumentException("Poll name cannot be empty");
        } else if (body.question() == null || body.question().isEmpty()) {
            throw new IllegalArgumentException("Poll question cannot be empty");
        } else if (body.options() == null || body.options().size() < 2) {
            throw new IllegalArgumentException("Require at least 2 poll options");
        } else if (body.options().size() > 7) {
            throw new IllegalArgumentException("Cannot have more than 7 poll options");
        }

        // Convert request body to model
        final Poll poll = new Poll();
        poll.setName(body.name());
        poll.setQuestion(body.question());
        body.options().forEach(optionText -> {
            final PollOption pollOption = new PollOption();
            pollOption.setDescription(optionText);
            pollOption.setPoll(poll);
            poll.getOptions().add(pollOption);
        });

        Poll savedPoll = pollRepo.save(poll);
        return activate ? setActivePoll(savedPoll.getId()) : savedPoll;
    }

    public record CreatePollRequestBody(
            String name,
            String question,
            List<String> options
    ) {}

    // Get sample create poll request body
    @Secured("ADMIN")
    @GetMapping("/sample")
    public @ResponseBody CreatePollRequestBody getSamplePoll() {
        return new CreatePollRequestBody(
                "A Riddle",
                "What have I got in my pocket?",
                Arrays.asList("Handses", "Knife", "String, or nothing")
        );
    }

    // Creates a sample poll
    @Secured("ADMIN")
    @PostMapping("/sample")
    public @ResponseBody Poll createSamplePoll(@RequestParam(name = "activate", required = false, defaultValue = "false") boolean activate) {
        return createPoll(getSamplePoll(), activate);
    }

    // Marks the given poll as active
    @Secured("ADMIN")
    @PutMapping("/activate/{id}")
    public @ResponseBody Poll setActivePoll(@PathVariable Integer id) {
        // Find the requested poll
        return pollRepo.findById(id)
                // Activate it
                .map(this::setActivePoll)
                // If not found, return error
                .orElseThrow(() -> new IllegalArgumentException("Poll not found"));
    }

    // Activates a new arbitrary poll
    @Secured("ADMIN")
    @PostMapping("/activate/next")
    public @ResponseBody Poll activateNextPoll() {
        // Find a poll that hasn't been activated yet
        return pollRepo.findFirstByStatus(PollStatus.PENDING)
                // Activate it
                .map(this::setActivePoll)
                // If none found, return error
                .orElseThrow(() -> new IllegalStateException("No pending polls"));
    }

    private Poll setActivePoll(Poll nextActivePoll) {
        // No action required if poll is already active
        if (nextActivePoll.getStatus() == PollStatus.ACTIVE) return nextActivePoll;

        // Close any active polls
        List<Poll> activePolls = pollRepo.findAllByStatus(PollStatus.ACTIVE);
        activePolls.forEach(poll -> poll.setStatus(PollStatus.CLOSED));
        pollRepo.saveAll(activePolls);

        // Update new active poll
        nextActivePoll.setStatus(PollStatus.ACTIVE);
        return pollRepo.save(nextActivePoll);
    }

    // Handles user response to a poll
    @PutMapping("/{pollId}/respond/{optionId}")
    public @ResponseBody Poll respondToPoll(@PathVariable Integer pollId, @PathVariable Integer optionId, @CookieValue(CookieController.USER_ID_COOKIE) String userId) {
        // Find required objects
        final Poll poll = pollRepo.findById(pollId).orElseThrow(() -> new IllegalArgumentException("Poll not found"));
        final PollOption option = poll.getOptions().stream().filter(opt -> opt.getId() == optionId).findFirst().orElseThrow(() -> new IllegalArgumentException("Option not found"));

        // Validation
        if (poll.getResponses().stream().anyMatch(response -> response.getUser().equals(userId))) {
            throw new IllegalCallerException("User has already responded to poll");
        }

        // Construct Response object
        Response response = new Response();
        response.setPoll(poll);
        response.setPollOption(option);
        response.setUser(userId);

        // Save
        responseRepo.save(response);

        // Respond with updated poll
        return getPollById(pollId, userId);
    }

}
