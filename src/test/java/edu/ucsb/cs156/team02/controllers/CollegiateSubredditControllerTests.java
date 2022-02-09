package edu.ucsb.cs156.team02.controllers;

import edu.ucsb.cs156.team02.repositories.UserRepository;
import edu.ucsb.cs156.team02.testconfig.TestConfig;
import edu.ucsb.cs156.team02.ControllerTestCase;
import edu.ucsb.cs156.team02.entities.CollegiateSubreddit;
import edu.ucsb.cs156.team02.entities.User;
import edu.ucsb.cs156.team02.repositories.CollegiateSubredditRepository;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = CollegiateSubredditController.class)
@Import(TestConfig.class)
public class CollegiateSubredditControllerTests extends ControllerTestCase {

    @MockBean
    CollegiateSubredditRepository collegiateSubredditRepository;

    @MockBean
    UserRepository userRepository;

    // Authorization tests for /api/collegiate_subreddits/all

    @Test
    public void api_collegiate_subreddit_all__logged_out__returns_403() throws Exception {
        mockMvc.perform(get("/api/collegiate_subreddits/all"))
                .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_collegiate_subreddit_all__user_logged_in__returns_200() throws Exception {
        mockMvc.perform(get("/api/collegiate_subreddits/all"))
                .andExpect(status().isOk());
    }

    // Authorization tests for /api/collegiate_subreddits/post

    @Test
    public void api_collegiate_subreddits_post__logged_out__returns_403() throws Exception {
        mockMvc.perform(post("/api/collegiate_subreddits/post"))
                .andExpect(status().is(403));
    }

    // Tests with mocks for database actions

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_collegiate_subreddits_all__user_logged_in__returns_only_collegiate_subreddits_for_user() throws Exception {

        // arrange

        User thisUser = currentUserService.getCurrentUser().getUser();

        CollegiateSubreddit collegiateSubreddit1 = CollegiateSubreddit.builder().name("Name 1").location("Location 1").subreddit("Subreddit 1").id(1L).build();
        CollegiateSubreddit collegiateSubreddit2 = CollegiateSubreddit.builder().name("Name 2").location("Location 2").subreddit("Subreddit 2").id(2L).build();

        ArrayList<CollegiateSubreddit> expectedCollegiateSubreddits = new ArrayList<>();
        expectedCollegiateSubreddits.addAll(Arrays.asList(collegiateSubreddit1, collegiateSubreddit2));
        when(collegiateSubredditRepository.findAll()).thenReturn(expectedCollegiateSubreddits);

        // act
        MvcResult response = mockMvc.perform(get("/api/collegiate_subreddits/all"))
                .andExpect(status().isOk()).andReturn();

        // assert

        verify(collegiateSubredditRepository, times(1)).findAll();
        String expectedJson = mapper.writeValueAsString(expectedCollegiateSubreddits);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_collegiate_subreddits_post__user_logged_in() throws Exception {
        // arrange

        User u = currentUserService.getCurrentUser().getUser();

        CollegiateSubreddit expectedCollegiateSubreddit = CollegiateSubreddit.builder()
                .name("test name")
                .location("test location")
                .subreddit("test sub")
                .id(0L)
                .build();

        when(collegiateSubredditRepository.save(eq(expectedCollegiateSubreddit))).thenReturn(expectedCollegiateSubreddit);

        // act
        MvcResult response = mockMvc.perform(
                post("/api/collegiate_subreddits/post?name=test name&location=test location&subreddit=test sub")
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(collegiateSubredditRepository, times(1)).save(expectedCollegiateSubreddit);
        String expectedJson = mapper.writeValueAsString(expectedCollegiateSubreddit);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "USER", "ADMIN" })
    @Test    
    public void api_CollegiateSubreddit__user_logged_in__returns_a_CollegiateSubreddit_that_exists() throws Exception {

        // arrange
        CollegiateSubreddit CollegiateSubreddit1 = CollegiateSubreddit.builder().
        name("CollegiateSubreddit 1").location("CollegiateSubreddit 1").subreddit("CollegiateSubreddit 1").id(123L).build();
        when(collegiateSubredditRepository.findById(eq(123L))).thenReturn(Optional.of(CollegiateSubreddit1));

        // act
        MvcResult response = mockMvc.perform(get("/api/collegiate_subreddits?id=123"))
                .andExpect(status().isOk()).andReturn();

        // assert

        verify(collegiateSubredditRepository, times(1)).findById(eq(123L));
        String expectedJson = mapper.writeValueAsString(CollegiateSubreddit1);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "USER","ADMIN" })
    @Test
    public void api_CollegiateSubreddit__user_logged_in__search_for_CollegiateSubreddit_that_does_not_exist() throws Exception {

        when(collegiateSubredditRepository.findById(eq(123L))).thenReturn(Optional.empty());

        MvcResult response = mockMvc.perform(get("/api/collegiate_subreddits?id=123")).andExpect(status().isBadRequest()).andReturn();

        verify(collegiateSubredditRepository,times(1)).findById(eq(123L));
        String responseString = response.getResponse().getContentAsString();
        assertEquals("record 123 not found",responseString);
    }

    @WithMockUser(roles = { "USER","ADMIN" })
    @Test
    public void api_CollegiateSubreddit__user_logged_in__delete_CollegiateSubreddit() throws Exception {
        // arrange

        CollegiateSubreddit CollegiateSubreddit1 = CollegiateSubreddit.builder().
        name("CollegiateSubreddit 1").location("CollegiateSubreddit 1").subreddit("CollegiateSubreddit 1").id(123L).build();
        when(collegiateSubredditRepository.findById(eq(123L))).thenReturn(Optional.of(CollegiateSubreddit1));

        // act
        MvcResult response = mockMvc.perform(
                delete("/api/collegiate_subreddits?id=123")
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(collegiateSubredditRepository, times(1)).findById(123L);
        verify(collegiateSubredditRepository, times(1)).deleteById(123L);
        String responseString = response.getResponse().getContentAsString();
        assertEquals("record 123 deleted", responseString);
    }

    @WithMockUser(roles = { "USER","ADMIN" })
    @Test
    public void api_CollegiateSubreddit__user_logged_in__delete_CollegiateSubreddit_that_does_not_exist() throws Exception {
        // arrange

        CollegiateSubreddit CollegiateSubreddit1 = CollegiateSubreddit.builder().name("CollegiateSubreddit 1").location("CollegiateSubreddit 1").subreddit("CollegiateSubreddit 1").id(123L).build();
        when(collegiateSubredditRepository.findById(eq(123L))).thenReturn(Optional.empty());

        // act
        MvcResult response = mockMvc.perform(
                delete("/api/collegiate_subreddits?id=123")
                        .with(csrf()))
                .andExpect(status().isBadRequest()).andReturn();

        // assert
        verify(collegiateSubredditRepository, times(1)).findById(123L);
        String responseString = response.getResponse().getContentAsString();
        assertEquals("record 123 not found", responseString);
    }

    @WithMockUser(roles = { "USER","ADMIN" })
    @Test
    public void api_CollegiateSubreddit__user_logged_in__put_CollegiateSubreddit() throws Exception {
        // arrange

        CollegiateSubreddit CollegiateSubreddit1 = CollegiateSubreddit.builder().
        name("CollegiateSubreddit 1").location("CollegiateSubreddit 1").subreddit("CollegiateSubreddit 1").id(123L).build();
        // We deliberately set the user information to another user
        // This shoudl get ignored and overwritten with currrent user when todo is saved

        CollegiateSubreddit updatedRecord = CollegiateSubreddit.builder().
        name("New CollegiateSubreddit").location("New CollegiateSubreddit").subreddit("New CollegiateSubreddit").id(123L).build();
        CollegiateSubreddit correctRecord = CollegiateSubreddit.builder().
        name("New CollegiateSubreddit").location("New CollegiateSubreddit").subreddit("New CollegiateSubreddit").id(123L).build();

        String requestBody = mapper.writeValueAsString(updatedRecord);
        String expectedReturn = mapper.writeValueAsString(correctRecord);

        when(collegiateSubredditRepository.findById(eq(123L))).thenReturn(Optional.of(CollegiateSubreddit1));

        // act
        MvcResult response = mockMvc.perform(
                put("/api/collegiate_subreddits?id=123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(requestBody)
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(collegiateSubredditRepository, times(1)).findById(123L);
        verify(collegiateSubredditRepository, times(1)).save(correctRecord); // should be saved with correct user
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedReturn, responseString);
    }

    @WithMockUser(roles = { "USER", "ADMIN" })
    @Test
    public void api_CollegiateSubreddit__user_logged_in__cannot_put_CollegiateSubreddit_that_does_not_exist() throws Exception {
        // arrange

        CollegiateSubreddit updatedRecord = CollegiateSubreddit.builder().
        name("New CollegiateSubreddit").location("New CollegiateSubreddit").subreddit("New CollegiateSubreddit").id(123L).build();

        String requestBody = mapper.writeValueAsString(updatedRecord);

        when(collegiateSubredditRepository.findById(eq(123L))).thenReturn(Optional.empty());

        // act
        MvcResult response = mockMvc.perform(
                put("/api/collegiate_subreddits?id=123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(requestBody)
                        .with(csrf()))
                .andExpect(status().isBadRequest()).andReturn();

        // assert
        verify(collegiateSubredditRepository, times(1)).findById(123L);
        String responseString = response.getResponse().getContentAsString();
        assertEquals("record 123 not found", responseString);
    }
}
