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
                        .with(csrf())).andExpect(status().isOk()).andReturn();

        // assert
        verify(UcsbRequirementRepository, times(1)).save(expectedUCSBRequirement);
        String expectedJson = mapper.writeValueAsString(expectedUCSBRequirement);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }


    @WithMockUser(roles = { "USER" })
    @Test
    public void api_todos__user_logged_in__delete_UCSBRequirement() throws Exception {
        // arrange

        // User otherUser = User.builder().id(98L).build();
        UCSBRequirement ucsbRequirement1 = UCSBRequirement.builder()
                .requirementCode("Test requirementCode").requirementTranslation("Test requirementTranslation").collegeCode("Test collegeCode").objCode("Test objCode").courseCount(1).units(1).inactive(true).id(15L).build();
        
        when(UcsbRequirementRepository.findById(eq(15L))).thenReturn(Optional.of(ucsbRequirement1));

        // act
        MvcResult response = mockMvc.perform(
                delete("/api/UCSBRequirements?id=15")
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(UcsbRequirementRepository, times(1)).findById(15L);
        verify(UcsbRequirementRepository, times(1)).deleteById(15L);
        String responseString = response.getResponse().getContentAsString();
        assertEquals("record 15 deleted", responseString);
    }



    @WithMockUser(roles = { "USER" })
    @Test
    public void api_todos__user_logged_in__delete_UCSBRequirement_that_does_not_exist() throws Exception {
        // arrange

        // User otherUser = User.builder().id(98L).build();
        UCSBRequirement ucsbRequirement1 = UCSBRequirement.builder()
                .requirementCode("Test requirementCode").requirementTranslation("Test requirementTranslation").collegeCode("Test collegeCode").objCode("Test objCode").courseCount(1).units(1).inactive(true).id(15L).build();
        
        when(UcsbRequirementRepository.findById(eq(15L))).thenReturn(Optional.empty());

        // act
        MvcResult response = mockMvc.perform(
                delete("/api/UCSBRequirements?id=15")
                        .with(csrf()))
                .andExpect(status().isBadRequest()).andReturn();

        // assert
        verify(UcsbRequirementRepository, times(1)).findById(15L);
        String responseString = response.getResponse().getContentAsString();
        assertEquals("record 15 not found", responseString);
    }
    @WithMockUser(roles = { "USER" })
    @Test
    public void api_UCSBRequirement__user_logged_in__put_UCSBRequirement() throws Exception {
        // arrange
    
        UCSBRequirement updatedUCSBReq = UCSBRequirement.builder().requirementCode("reqCode").requirementTranslation("reqTrans").collegeCode("cCode").objCode("oCode")
        .courseCount(1).units(4).inactive(true).id(123L).build();
    
        UCSBRequirement correctUCSBReq = UCSBRequirement.builder().requirementCode("reqCode").requirementTranslation("reqTrans").collegeCode("cCode").objCode("oCode")
        .courseCount(1).units(4).inactive(true).id(123L).build();
    
        String requestBody = mapper.writeValueAsString(updatedUCSBReq);
        String expectedReturn = mapper.writeValueAsString(correctUCSBReq);
    
        when(UcsbRequirementRepository.findById(eq(123L))).thenReturn(Optional.of(correctUCSBReq));
    
        // act
        MvcResult response = mockMvc.perform(
                put("/api/UCSBRequirements?id=123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(requestBody)
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();
    
        // assert
        verify(UcsbRequirementRepository, times(1)).findById(123L);
        verify(UcsbRequirementRepository, times(1)).save(correctUCSBReq); // should be saved with correct user
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedReturn, responseString);
    }
    
    @WithMockUser(roles = { "USER" })
    @Test
    public void api_UCSBRequirements__user_logged_in__cannot_put_UCSBRequirements_that_does_not_exist() throws Exception {
        // arrange
    
        UCSBRequirement updatedUCSBReq = UCSBRequirement.builder().requirementCode("reqCode").requirementTranslation("reqTrans")
        .collegeCode("cCode").objCode("oCode").courseCount(1).units(4).inactive(true).id(123L).build();
    
        String requestBody = mapper.writeValueAsString(updatedUCSBReq);
    
        when(UcsbRequirementRepository.findById(eq(123L))).thenReturn(Optional.empty());
    
        // act
        MvcResult response = mockMvc.perform(
                put("/api/UCSBRequirements?id=123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(requestBody)
                        .with(csrf()))
                .andExpect(status().isBadRequest()).andReturn();
    
        // assert
        verify(UcsbRequirementRepository, times(1)).findById(123L);
        String responseString = response.getResponse().getContentAsString();
        assertEquals("record 123 not found", responseString);
    }
    
}
