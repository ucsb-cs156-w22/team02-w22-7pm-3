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


    public class UCSBRequirementOrError {
        Long id;
        UCSBRequirement ucsbRequirement;
        ResponseEntity<String> error;

        public UCSBRequirementOrError(Long id) {
            this.id = id;
        }
    }
    
    @Autowired
    private UCSBRequirementRepository ucsbRequirementRepository;

    @Autowired
    ObjectMapper mapper;

    // @ApiOperation(value = "List all UCSBRequirement")  
    // @PreAuthorize("hasRole('ROLE_ADMIN')")
    // @GetMapping("/admin/all")
    // public Iterable<UCSBRequirement> allUsersUCSBRequirements() {
    //     loggingService.logMethod();
    //     Iterable<UCSBRequirement> UCSBRequirement = ucsbRequirementRepository.findAll();
    //     return UCSBRequirement;
    // }

    @ApiOperation(value = "List all UCSBRequirement")  
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<UCSBRequirement> thisUsersUCSBRequirements() {
        loggingService.logMethod();
        // CurrentUser currentUser = getCurrentUser();
        // Iterable<UCSBRequirement> UCSBRequirement = ucsbRequirementRepository.findAllByUserId(currentUser.getUser().getId());
        Iterable<UCSBRequirement> UCSBRequirement = ucsbRequirementRepository.findAll();
        return UCSBRequirement;
    }

    @ApiOperation(value = "Create a new UCSBRequirement") 
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/post")
    public UCSBRequirement postUCSBRequirement(
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
    }


    @ApiOperation(value = "Delete a UCSBRequirement")
    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping("")
    public ResponseEntity<String> deleteUCSBRequirement(
            @ApiParam("id") @RequestParam Long id) {
        loggingService.logMethod();

        UCSBRequirementOrError toe = new UCSBRequirementOrError(id);

        toe = doesUCSBRequirementExist(toe);
        if (toe.error != null) {
            return toe.error;
        }

        ucsbRequirementRepository.deleteById(id);

        return ResponseEntity.ok().body(String.format("record %d deleted", id));

    }

    /**
     * Pre-conditions: toe.id is value to look up, toe.UCSBRequirement and toe.error are null
     * 
     * Post-condition: if UCSBRequirement with id toe.id exists, toe.UCSBRequirement now refers to it, and
     * error is null.
     * Otherwise, UCSBRequirement with id toe.id does not exist, and error is a suitable return
     * value to
     * report this error condition.
     */
    public UCSBRequirementOrError doesUCSBRequirementExist(UCSBRequirementOrError toe) {

        Optional<UCSBRequirement> optionalUCSBRequirement = ucsbRequirementRepository.findById(toe.id);

        if (optionalUCSBRequirement.isEmpty()) {
            toe.error = ResponseEntity
                    .badRequest()
                    .body(String.format("record %d not found", toe.id));
        } else {
            toe.ucsbRequirement = optionalUCSBRequirement.get();
        }
        return toe;
    }

    /**
     * Pre-conditions: toe.UCSBRequirement is non-null and refers to the UCSBRequirement with id toe.id,
     * and toe.error is null
     * 
     * Post-condition: if UCSBRequirement belongs to current user, then error is still null.
     * Otherwise error is a suitable
     * return value.
     */

    // public UCSBRequirementOrError doesUCSBRequirementBelongToCurrentUser(UCSBRequirementOrError toe) {
    //     CurrentUser currentUser = getCurrentUser();
    //     log.info("currentUser={}", currentUser);

    //     Long currentUserId = currentUser.getUser().getId();
    //     Long UCSBRequirementUserId = toe.ucsbRequirement.getUser().getId();
    //     log.info("currentUserId={} UCSBRequirementUserId={}", currentUserId, UCSBRequirementUserId);

    //     if (UCSBRequirementUserId != currentUserId) {
    //         toe.error = ResponseEntity
    //                 .badRequest()
    //                 .body(String.format("UCSBRequirement with id %d not found", toe.id));
    //     }
    //     return toe;
    // }

}


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