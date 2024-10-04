package com.dizplai.dizplai_tech_test.controller;

import com.dizplai.dizplai_tech_test.config.ExceptionResolver;
import com.dizplai.dizplai_tech_test.dao.PollRepository;
import com.dizplai.dizplai_tech_test.dao.ResponseRepository;
import com.dizplai.dizplai_tech_test.model.Poll;
import com.dizplai.dizplai_tech_test.model.PollOption;
import com.dizplai.dizplai_tech_test.model.enums.PollStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class PollControllerTest {

    private MockMvc mvc;

    @InjectMocks
    private PollController pollController;

    @Mock
    private PollRepository pollRepo;

    @Mock
    private ResponseRepository responseRepo;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .standaloneSetup(pollController)
                .setControllerAdvice(new ExceptionResolver())
                .build();
    }

    @Test
    public void getActivePollTest() throws Exception {
        Poll poll = samplePoll();
        poll.setStatus(PollStatus.ACTIVE);
        when(pollRepo.findFirstByStatus(PollStatus.ACTIVE)).thenReturn(Optional.of(poll));

        mvc.perform(get("/poll"))
                .andExpect(status().isOk())
                .andExpect(content().string(samplePollOutput(PollStatus.ACTIVE)));
    }

    @Test
    public void getActivePollTest_noneActive() throws Exception {
        when(pollRepo.findFirstByStatus(PollStatus.ACTIVE)).thenReturn(Optional.empty());
        mvc.perform(get("/poll"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    public void createPollTest_unauthorized() throws Exception {
        String requestBody = "{" +
                "\"name\": \"A Riddle\"," +
                "\"question\": \"What have I got in my pocket?\"," +
                "\"options\":[\"Handses\", \"Knife\", \"String, or nothing\"]" +
                "}";

        mvc.perform(post("/poll").content(requestBody))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void createPollTest_authorized() throws Exception {
        when(pollRepo.save(any(Poll.class))).then(invocation -> {
            Poll poll = invocation.getArgument(0);
            poll.setId(1);
            int optionId = 1;
            for (PollOption option : poll.getOptions()) {
                option.setId(optionId++);
            }
            return poll;
        });

        String requestBody = "{" +
                "\"name\": \"A Riddle\"," +
                "\"question\": \"What have I got in my pocket?\"," +
                "\"options\":[\"Handses\", \"Knife\", \"String, or nothing\"]" +
                "}";

        mvc.perform(post("/poll").content(requestBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(samplePollOutput(PollStatus.PENDING)));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void createPollTest_noOptions() throws Exception {
        String requestBody = "{" +
                "\"name\": \"A Riddle\"," +
                "\"question\": \"What have I got in my pocket?\"," +
                "\"options\":[]" +
                "}";

        mvc.perform(post("/poll").content(requestBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    private Poll samplePoll() {
        Poll poll = new Poll();
        poll.setId(1);
        poll.setName("A Riddle");
        poll.setQuestion("What have I got in my pocket?");

        PollOption option1 = new PollOption();
        option1.setId(1);
        option1.setDescription("Handses");
        option1.setPoll(poll);
        poll.getOptions().add(option1);

        PollOption option2 = new PollOption();
        option2.setId(2);
        option2.setDescription("Knife");
        option2.setPoll(poll);
        poll.getOptions().add(option2);

        PollOption option3 = new PollOption();
        option3.setId(3);
        option3.setDescription("String, or nothing");
        option3.setPoll(poll);
        poll.getOptions().add(option3);

        return poll;
    }

    private String samplePollOutput(PollStatus pollStatus) {
        return "{" +
                "\"id\":1," +
                "\"name\":\"A Riddle\"," +
                "\"question\":\"What have I got in my pocket?\"," +
                "\"options\":[{" +
                    "\"id\":1," +
                    "\"description\":\"Handses\"" +
                "},{" +
                    "\"id\":2," +
                    "\"description\":\"Knife\"" +
                "},{" +
                    "\"id\":3," +
                    "\"description\":\"String, or nothing\"" +
                "}]," +
                "\"status\":\"" + pollStatus.name() + "\"" +
        "}";
    }

}
