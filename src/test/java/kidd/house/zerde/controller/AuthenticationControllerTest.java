package kidd.house.zerde.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kidd.house.zerde.dto.adminDto.ChangePasswordDto;
import kidd.house.zerde.dto.registration.RefreshTokenRequest;
import kidd.house.zerde.dto.registration.SignInRequest;
import kidd.house.zerde.dto.registration.SignUpRequest;
import kidd.house.zerde.service.AuthenticationService;
import kidd.house.zerde.service.ChangeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {
    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private ChangeService changeService;
    @InjectMocks
    private AuthenticationController authenticationController;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders.standaloneSetup(authenticationController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void signUp() throws Exception {
        SignUpRequest user = new SignUpRequest("user","user@gmail.com","user");
        String userJson = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isCreated());
        verify(authenticationService, times(1)).signUp(user);
    }

    @Test
    void signIn() throws Exception {
        SignInRequest user = new SignInRequest("user@gmail.com","user");
        String userJson = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/api/v1/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isFound());
        verify(authenticationService, times(1)).signIn(user);
    }

    @Test
    void refresh() throws Exception {
        RefreshTokenRequest refresh = new RefreshTokenRequest("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJpYXQiOjE3NDkwMjI5OTgsImV4cCI6MTc0OTAyNDQzOH0.Uj3Wl-C5eCVlgQ49CVeAvvuuFvZfFtZqmnWxgMK_T9A");
        String userJson = objectMapper.writeValueAsString(refresh);
        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isContinue());
        verify(authenticationService, times(1)).refreshToken(refresh);
    }

    @Test
    void changePassword() throws Exception {
        ChangePasswordDto change = new ChangePasswordDto("user@gmail.com","gbd89dbh","user");
        String userJson = objectMapper.writeValueAsString(change);
        mockMvc.perform(post("/api/v1/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk());
        verify(changeService, times(1)).changePassword(change);
    }
}