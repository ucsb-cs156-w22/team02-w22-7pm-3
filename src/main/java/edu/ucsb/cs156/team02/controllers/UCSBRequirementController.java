package edu.ucsb.cs156.team02.controllers;

import edu.ucsb.cs156.team02.entities.UCSBRequirement;
import edu.ucsb.cs156.team02.entities.User;
import edu.ucsb.cs156.team02.models.CurrentUser;
import edu.ucsb.cs156.team02.repositories.UCSBRequirementRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;

@Api(description = "UCSBReqirements")
@RequestMapping("/api/UCSBRequirements")
@RestController
@Slf4j
public class UCSBRequirementController extends ApiController {

    
    @Autowired
    private UCSBRequirementRepository ucsbRequirementRepository;

    @Autowired
    ObjectMapper mapper;

    @ApiOperation(value = "List all UCSBRequirement (if it belongs to current user)")  
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<UCSBRequirement> getUCSBRequirements() {
        loggingService.logMethod();
        Iterable<UCSBRequirement> UCSBRequirement = ucsbRequirementRepository.findAll();
        return UCSBRequirement;
    }


    @ApiOperation(value = "Create a new UCSBRequirement") 
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/post")
    public UCSBRequirement postUcsbRequirement(
            // @ApiParam("id") @RequestParam Long id,
            @ApiParam("GE Letter") @RequestParam String requirementCode,
            @ApiParam("Subject Type") @RequestParam String requirementTranslation,
            @ApiParam("Only 'ENGR' or 'L&S' or 'UCSB'") @RequestParam String collegeCode,
            @ApiParam("Degree Type") @RequestParam String objCode,
            @ApiParam("Course Count") @RequestParam Integer courseCount,      //Description names should be improved
            @ApiParam("Units") @RequestParam Integer units,
            @ApiParam("Inactive") @RequestParam Boolean inactive) {
        loggingService.logMethod();
        // CurrentUser currentUser = getCurrentUser();
        // log.info("currentUser={}", currentUser);

        UCSBRequirement ucsbRequirement = new UCSBRequirement();
        // ucsbRequirement.setUser(currentUser.getUser());
        // ucsbRequirement.setId(id);
        ucsbRequirement.setRequirementCode(requirementCode);
        ucsbRequirement.setRequirementTranslation(requirementTranslation);
        ucsbRequirement.setCollegeCode(collegeCode);
        ucsbRequirement.setObjCode(objCode);
        ucsbRequirement.setCourseCount(courseCount);
        ucsbRequirement.setUnits(units);
        ucsbRequirement.setInactive(inactive);
        UCSBRequirement savedUcsbRequirement = ucsbRequirementRepository.save(ucsbRequirement);
        return savedUcsbRequirement;

        
        // private long id;
        // private String requirementCode;
        // private String requirementTranslation;
        // private String collegeCode;
        // private String objCode;
        // private int courseCount;
        // private int units; 
        // private boolean inactive;
        
        // [
        //     {
        //       "requirementCode": "A1",
        //       "requirementTranslation": "English Reading & Composition",
        //       "collegeCode": "ENGR",
        //       "objCode": "BS",
        //       "courseCount": 1,
        //       "units": 4,
        //       "inactive": false
        //     }]


    }




}