package kidd.house.zerde.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kidd.house.zerde.dto.signupLesson.FreeLesson;
import kidd.house.zerde.dto.signupLesson.LessonTypeDto;
import kidd.house.zerde.dto.signupLesson.SignUpLessonResponse;
import kidd.house.zerde.dto.signupLesson.SignupRequestDto;
import kidd.house.zerde.model.record.LessonDay;
import kidd.house.zerde.service.SignupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@ExtendWith(MockitoExtension.class)
class SignupControllerTest {

    @Mock
    private SignupService signupService;

    @InjectMocks
    private SignupController signupController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(signupController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void signUpLesson_ReturnsLessonList() throws Exception {
//        // prepare test data
//        List<SignUpLessonResponse> lessonResponses = List.of(
//                new SignUpLessonResponse(
//                        new LessonDay("2025-06-13"),
//                        new LessonTypeDto(
//                                "буква B",
//                                "GROUP",
//                                List.of(new FreeLesson("10:00", "10:30"))
//                        )
//                )
//        );
//
//        // define mock behavior
//        Mockito.when(signupService.getLessons()).thenReturn(lessonResponses);
//
//        // perform request and assert
//        mockMvc.perform(get("/api/v2/signup-lesson"))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].lessonDay.day").value("2025-06-13"))
//                .andExpect(jsonPath("$[0].lessonTypeDto.name").value("буква B"))
//                .andExpect(jsonPath("$[0].lessonTypeDto.groupType").value("GROUP"))
//                .andExpect(jsonPath("$[0].lessonTypeDto.freeLessons[0].start").value("10:00"))
//                .andExpect(jsonPath("$[0].lessonTypeDto.freeLessons[0].end").value("10:30"));
//
//        // verify service call
//        Mockito.verify(signupService, times(1)).getLessons();
    }

    @Test
    void createSignupLesson() throws Exception {
        SignupRequestDto signupRequestDto = new SignupRequestDto(
                "Gowa",12,"Petya","+77024562150","petya@gmail.com",
                new LessonTypeDto(
                        "bukva B","GROUP",
                        List.of(new FreeLesson("10:00","10:30"))
                ),"2025.06.16");
        String userJson = objectMapper.writeValueAsString(signupRequestDto);
        mockMvc.perform(post("/api/v2/signup-lesson")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated());
        verify(signupService, times(1)).saveSignup(signupRequestDto,"success");
        verify(signupService, times(1)).sendNotification(signupRequestDto);
    }
}