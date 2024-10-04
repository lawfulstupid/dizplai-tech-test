package com.dizplai.dizplai_tech_test.controller;

import com.dizplai.dizplai_tech_test.dao.PollRepository;
import com.dizplai.dizplai_tech_test.dao.ResponseRepository;
import com.dizplai.dizplai_tech_test.model.PollOption;
import com.dizplai.dizplai_tech_test.model.Poll;
import com.dizplai.dizplai_tech_test.model.Response;
import com.dizplai.dizplai_tech_test.model.enums.PollStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping(path = "/poll")
@RequiredArgsConstructor
public class PollController {

    private final PollRepository pollRepo;
    private final ResponseRepository responseRepo;

    // Returns active poll
    @GetMapping
    public @ResponseBody Poll getActivePoll() {
        return pollRepo.findFirstByStatus(PollStatus.ACTIVE).orElse(null);
    }

    // Creates a new poll
    @Secured("ADMIN")
    @PostMapping
    public @ResponseBody Poll createPoll(@RequestBody CreatePollRequestBody body) {
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

        return pollRepo.save(poll);
    }

    public record CreatePollRequestBody(
            String name,
            String question,
            List<String> options
    ) {}


    // Marks the given poll as active
    @Secured("ADMIN")
    @PutMapping("/activate/{id}")
    public @ResponseBody Poll setActivePoll(@PathVariable Integer id) {
        final Poll newActivePoll = pollRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Poll not found"));

        // Close any active polls
        List<Poll> activePolls = pollRepo.findAllByStatus(PollStatus.ACTIVE);
        activePolls.forEach(poll -> {
            if (poll.getId() == newActivePoll.getId()) {
                return; // no action required
            }
            poll.setStatus(PollStatus.CLOSED);
        });
        pollRepo.saveAll(activePolls);

        // Update new active poll
        newActivePoll.setStatus(PollStatus.ACTIVE);
        return pollRepo.save(newActivePoll);
    }

    // Activates a new arbitrary poll
    @Secured("ADMIN")
    @PostMapping("/activate/next")
    public @ResponseBody Poll activateNextPoll() {
        // Find a poll that hasn't been activated yet
        return pollRepo.findFirstByStatus(PollStatus.PENDING)
                // Activate it
                .map(nextPoll -> setActivePoll(nextPoll.getId()))
                // If none found, don't close existing poll
                .orElse(null);
    }

    // Gets a poll
    @Secured("ADMIN")
    @GetMapping("/{id}")
    public @ResponseBody Poll getPoll(@PathVariable Integer id) {
        return pollRepo.findById(id).orElse(null);
    }

    // Handles user response to a poll
    @PostMapping("/{pollId}/respond/{optionId}")
    public ResponseEntity<Void> respondToPoll(@PathVariable Integer pollId, @PathVariable Integer optionId, @CookieValue(CookieController.USER_ID_COOKIE) String userId) {
        // Find required objects
        final Poll poll = pollRepo.findById(pollId).orElseThrow(() -> new IllegalArgumentException("Poll not found"));
        final PollOption option = poll.getOptions().stream().filter(opt -> opt.getId() == optionId).findFirst().orElseThrow(() -> new IllegalArgumentException("Option not found"));

        // Construct Response object
        Response response = new Response();
        response.setPoll(poll);
        response.setPollOption(option);
        response.setUser(userId);

        // Save
        responseRepo.save(response);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // Returns aggregated results for a poll
    @GetMapping("/{id}/results")
    public @ResponseBody PollResult getPollResults(@PathVariable Integer id, @CookieValue(CookieController.USER_ID_COOKIE) String userId) {
        final Poll poll = pollRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Poll not found"));

        final int totalVotes = poll.getResponses().size();

        // Convert Poll to PollResult
        AtomicBoolean foundUserVote = new AtomicBoolean(false);
        List<PollOptionResult> optionResults = poll.getOptions().stream().map(option -> {
            double votePercentage = (double) option.getResponses().size() / totalVotes;
            boolean userSelection = false;
            if (!foundUserVote.get()) {
                userSelection = !foundUserVote.get() && option.getResponses().stream().map(Response::getUser).anyMatch(userId::equals);
                foundUserVote.set(userSelection);
            }

            // Convert PollOption to PollOptionResult
            return new PollOptionResult(
                    option.getDescription(),
                    option.getResponses().size(),
                    votePercentage,
                    userSelection
            );
        }).collect(Collectors.toList());

        if (!foundUserVote.get()) {
            throw new IllegalCallerException("You must cast a vote to view the results");
        }

        return new PollResult(
                poll.getName(),
                poll.getQuestion(),
                totalVotes,
                optionResults
        );
    }

    public record PollResult(
            String name,
            String question,
            int responses,
            List<PollOptionResult> options
    ) {}

    public record PollOptionResult(
            String description,
            int votes,
            double votesPercentage,
            boolean userSelection
    ) {}

}
