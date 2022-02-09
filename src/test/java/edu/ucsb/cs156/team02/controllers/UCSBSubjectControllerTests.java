package edu.ucsb.cs156.team02.controllers;

import edu.ucsb.cs156.team02.repositories.UserRepository;
import edu.ucsb.cs156.team02.testconfig.TestConfig;
import edu.ucsb.cs156.team02.ControllerTestCase;
import edu.ucsb.cs156.team02.entities.UCSBSubject;
// import edu.ucsb.cs156.team02.entities.User;
import edu.ucsb.cs156.team02.repositories.UCSBSubjectRepository;

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

@WebMvcTest(controllers = UCSBSubjectController.class)
@Import(TestConfig.class)
public class UCSBSubjectControllerTests extends ControllerTestCase {

    @MockBean
    UCSBSubjectRepository ucsbSubjectRepository;

    @MockBean
    UserRepository userRepository;

    @Test
    public void api_UCSBSubjects_get__all__returns_403__logged_out() throws Exception {
        mockMvc.perform(get("/api/UCSBSubjects/all"))
                .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_UCSBSubjects_get__all__returns_200__logged_in() throws Exception {
        mockMvc.perform(get("/api/UCSBSubjects/all"))
                .andExpect(status().isOk());
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_UCSBSubject_all__user_logged_in__returns_UCSBSubjects_for_user() throws Exception {
        UCSBSubject s1 = UCSBSubject.builder().subjectCode("testSubjectCode").subjectTranslation("testSubjectTranslation").deptCode("testDeptCode").CollegeCode("testCollegeCode").relatedDeptCode("testRelatedDeptCode").inactive(true).build();
        UCSBSubject s2 = UCSBSubject.builder().subjectCode("testSubjectCode2").subjectTranslation("testSubjectTranslation2").deptCode("testDeptCode2").CollegeCode("testCollegeCode2").relatedDeptCode("testRelatedDeptCode2").inactive(true).build();
        ArrayList<UCSBSubject> expectedUCSBSubjects = new ArrayList<>();
        expectedUCSBSubjects.addAll(Arrays.asList(s1,s2));
        when(ucsbSubjectRepository.findAll()).thenReturn(expectedUCSBSubjects);
        
        MvcResult response = mockMvc.perform(get("/api/UCSBSubjects/all")).andExpect(status().isOk()).andReturn();
        
        verify(ucsbSubjectRepository,times(1)).findAll();
        String expectedJSON = mapper.writeValueAsString(expectedUCSBSubjects);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJSON,responseString);   
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_UCSBSubject_post__user_logged_in() throws Exception {
        UCSBSubject expectedUCSBSubject = UCSBSubject.builder().subjectCode("testSubjectCode").subjectTranslation("testSubjectTranslation").deptCode("testDeptCode").CollegeCode("testCollegeCode").relatedDeptCode("testRelatedDeptCode").inactive(true).build();
        when(ucsbSubjectRepository.save(eq(expectedUCSBSubject))).thenReturn(expectedUCSBSubject);
        
        MvcResult response = mockMvc.perform(post("/api/UCSBSubjects/post?collegeCode=testCollegeCode&deptCode=testDeptCode&inactive=true&relatedDeptCode=testRelatedDeptCode&subjectCode=testSubjectCode&subjectTranslation=testSubjectTranslation").with(csrf())).andExpect(status().isOk()).andReturn();
        
        verify(ucsbSubjectRepository,times(1)).save(expectedUCSBSubject);
        String expectedJSON = mapper.writeValueAsString(expectedUCSBSubject);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJSON,responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_UCSBSubject__user_logged_in__search_for_UCSBSubject() throws Exception {
        
        UCSBSubject ucsbSubject = UCSBSubject.builder().subjectCode("testSubjectCode").subjectTranslation("testSubjectTranslation").deptCode("testDeptCode").CollegeCode("testCollegeCode").relatedDeptCode("testRelatedDeptCode").inactive(true).id(123L).build();
        when(ucsbSubjectRepository.findById(eq(123L))).thenReturn(Optional.of(ucsbSubject));

        MvcResult response = mockMvc.perform(get("/api/UCSBSubjects?id=123")).andExpect(status().isOk()).andReturn();
            
        verify(ucsbSubjectRepository,times(1)).findById(eq(123L));
        String expectedJSON = mapper.writeValueAsString(ucsbSubject);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJSON,responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_UCSBSubject__user_logged_in__search_for_UCSBSubject_that_does_not_exist() throws Exception {
        
        when(ucsbSubjectRepository.findById(eq(123L))).thenReturn(Optional.empty());

        MvcResult response = mockMvc.perform(get("/api/UCSBSubjects?id=123")).andExpect(status().isBadRequest()).andReturn();
        
        verify(ucsbSubjectRepository,times(1)).findById(eq(123L));
        String responseString = response.getResponse().getContentAsString();
        assertEquals("id 123 not found",responseString);
    }
    //tests for PUT for the UCSBSubject Controller
    @WithMockUser(roles = { "USER" })
    @Test
    public void api_todos__user_logged_in__put_UCSBSubject() throws Exception {
        // arrange

        
        // User u = currentUserService.getCurrentUser().getUser();
        // User otherUser = User.builder().id(999).build();
        UCSBSubject ucsbSubject1 = UCSBSubject.builder().subjectCode("testSubjectCode 1").subjectTranslation("testSubjectTranslation 1").deptCode("testDeptCode 1").CollegeCode("testCollegeCode").relatedDeptCode("testRelatedDeptCode").id(123L).build();
        
        // // We deliberately set the user information to another user
        // This shoudl get ignored and overwritten with currrent user when todo is saved

        UCSBSubject updatedUCSBSubject = UCSBSubject.builder().subjectCode("testSubjectCode").subjectTranslation("testSubjectTranslation").deptCode("testDeptCode").CollegeCode("testCollegeCode").relatedDeptCode("testRelatedDeptCode").id(123L).build();
        UCSBSubject correctUCSBSubject = UCSBSubject.builder().subjectCode("testSubjectCode").subjectTranslation("testSubjectTranslation").deptCode("testDeptCode").CollegeCode("testCollegeCode").relatedDeptCode("testRelatedDeptCode").id(123L).build();

        String requestBody = mapper.writeValueAsString(updatedUCSBSubject);
        String expectedReturn = mapper.writeValueAsString(correctUCSBSubject);

        when(ucsbSubjectRepository.findById(eq(123L))).thenReturn(Optional.of(ucsbSubject1));

        // act
        MvcResult response = mockMvc.perform(
                put("/api/UCSBSubjects?id=123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(requestBody)
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(ucsbSubjectRepository, times(1)).findById(123L);
        verify(ucsbSubjectRepository, times(1)).save(correctUCSBSubject); // should be saved with correct user
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedReturn, responseString);
    }
    

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_UCSBSubject__user_logged_in__delete_UCSBSubject() throws Exception {
        UCSBSubject ucsbSubject = UCSBSubject.builder().subjectCode("testSubjectCode").subjectTranslation("testSubjectTranslation").deptCode("testDeptCode").CollegeCode("testCollegeCode").relatedDeptCode("testRelatedDeptCode").inactive(true).id(123L).build();
        when(ucsbSubjectRepository.findById(eq(123L))).thenReturn(Optional.of(ucsbSubject));

        MvcResult response =  mockMvc.perform(
            delete("/api/UCSBSubjects?id=123")
                    .with(csrf()))
            .andExpect(status().isOk()).andReturn();

            verify(ucsbSubjectRepository, times(1)).findById(123L);
            verify(ucsbSubjectRepository, times(1)).deleteById(123L);
            String responseString = response.getResponse().getContentAsString();
            assertEquals("record 123 deleted", responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_todos__user_logged_in__cannot_put_UCSBSubject_that_does_not_exist() throws Exception {
        // arrange

        //UCSBSubject updatedUCSBSubject = UCSBSubject.builder().title("New Title").details("New Details").done(true).id(123L).build();
        UCSBSubject updatedUCSBSubject = UCSBSubject
        .builder().subjectCode("testSubjectCode").subjectTranslation("testSubjectTranslation").deptCode("testDeptCode").CollegeCode("testCollegeCode").relatedDeptCode("testRelatedDeptCode").id(123L).build();

        String requestBody = mapper.writeValueAsString(updatedUCSBSubject);

        when(ucsbSubjectRepository.findById(eq(123L))).thenReturn(Optional.empty());

        // act
        MvcResult response = mockMvc.perform(
                put("/api/UCSBSubjects?id=123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(requestBody)
                        .with(csrf()))
                .andExpect(status().isBadRequest()).andReturn();

        // assert
        verify(ucsbSubjectRepository, times(1)).findById(123L);
        String responseString = response.getResponse().getContentAsString();
        assertEquals("id 123 not found", responseString);
    }
    
    @WithMockUser(roles = { "USER" })
    @Test
    public void api_UCSBSubject__user_logged_in__delete_UCSBSubject_that_does_not_exist() throws Exception {
        when(ucsbSubjectRepository.findById(eq(123L))).thenReturn(Optional.empty());

        MvcResult response = mockMvc.perform(
        delete("/api/UCSBSubjects?id=123")
                .with(csrf()))
        .andExpect(status().isBadRequest()).andReturn();
        
        verify(ucsbSubjectRepository,times(1)).findById(eq(123L));
        String responseString = response.getResponse().getContentAsString();
        assertEquals("id 123 not found",responseString);
    }
}