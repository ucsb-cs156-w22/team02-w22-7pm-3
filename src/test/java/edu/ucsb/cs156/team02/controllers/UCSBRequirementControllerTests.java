package edu.ucsb.cs156.team02.controllers;

import edu.ucsb.cs156.team02.repositories.UserRepository;
import edu.ucsb.cs156.team02.testconfig.TestConfig;
import edu.ucsb.cs156.team02.ControllerTestCase;
import edu.ucsb.cs156.team02.entities.UCSBRequirement;
import edu.ucsb.cs156.team02.entities.User;
import edu.ucsb.cs156.team02.repositories.UCSBRequirementRepository;

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

@WebMvcTest(controllers = UCSBRequirementController.class)
@Import(TestConfig.class)
public class UCSBRequirementControllerTests extends ControllerTestCase {

    @MockBean
    UCSBRequirementRepository UcsbRequirementRepository;

    @MockBean
    UserRepository userRepository;


    //authorization tests
    @Test
    public void api_UCSBRequirements_get__all__returns_403_logged_out() throws Exception {
        mockMvc.perform(get("/api/UCSBRequirements/all"))
                .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_UCSBRequirements_get__all__returns_200_user_logged_in() throws Exception {
        mockMvc.perform(get("/api/UCSBRequirements/all"))
                .andExpect(status().isOk());
    }

    // @WithMockUser(roles = { "ADMIN" })
    // @Test
    // public void api_UCSBRequirements_get__all__returns_200_admin_logged_in() throws Exception {
    //     mockMvc.perform(get("/api/UCSBRequirements/all"))
    //             .andExpect(status().isOk());
    // }


    @WithMockUser(roles = { "USER" })
    @Test
    public void api_UCSBRequirement_all__user_logged_in__returns_UCSBRequirements_for_user() throws Exception {

        // arrange

        // User thisUser = currentUserService.getCurrentUser().getUser();

        // Todo todo1 = Todo.builder().title("Todo 1").details("Todo 1").done(false).user(thisUser).id(1L).build();
        // Todo todo2 = Todo.builder().title("Todo 2").details("Todo 2").done(false).user(thisUser).id(2L).build();
        UCSBRequirement ucsbrequirement1 = UCSBRequirement.builder().requirementCode("Test requirementCode").requirementTranslation("Test requirementTranslation").collegeCode("Test collegeCode").objCode("Test objCode").courseCount(1).units(1).inactive(true).id(0L).build();
        UCSBRequirement ucsbrequirement2 = UCSBRequirement.builder().requirementCode("Test requirementCode").requirementTranslation("Test requirementTranslation").collegeCode("Test collegeCode").objCode("Test objCode").courseCount(1).units(1).inactive(true).id(0L).build();


        ArrayList<UCSBRequirement> expectedUCSBRequirements = new ArrayList<>();
        expectedUCSBRequirements.addAll(Arrays.asList(ucsbrequirement1, ucsbrequirement2));
        when(UcsbRequirementRepository.findAll()).thenReturn(expectedUCSBRequirements);      //findByUserId(thisUser.getId())).thenReturn(expectedTodos);

        // act
        MvcResult response = mockMvc.perform(get("/api/UCSBRequirements/all"))
                .andExpect(status().isOk()).andReturn();

        // assert

        verify(UcsbRequirementRepository, times(1)).findAll();         //findByUserId(eq(thisUser.getId()));
        String expectedJson = mapper.writeValueAsString(expectedUCSBRequirements);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }


    @WithMockUser(roles = { "USER" })
    @Test
    public void api_UCSBRequirement_post__user_logged_in() throws Exception {
        // arrange

        // User u = currentUserService.getCurrentUser().getUser();

        UCSBRequirement expectedUCSBRequirement = UCSBRequirement.builder()
                .requirementCode("Test requirementCode")
                .requirementTranslation("Test requirementTranslation")
                .collegeCode("Test collegeCode")
                .objCode("Test objCode")
                .courseCount(1)
                .units(1)
                .inactive(true)
                .id(0L)
                .build();

        when(UcsbRequirementRepository.save(eq(expectedUCSBRequirement))).thenReturn(expectedUCSBRequirement);

        // act
        MvcResult response = mockMvc.perform(
                post("/api/UCSBRequirements/post?requirementCode=Test " +  
                "requirementCode&requirementTranslation=Test requirementTranslation&collegeCode=Test collegeCode&objCode=Test objCode"+
                "&courseCount=1&units=1&inactive=true")
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(UcsbRequirementRepository, times(1)).save(expectedUCSBRequirement);
        String expectedJson = mapper.writeValueAsString(expectedUCSBRequirement);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }


}
