package edu.ucsb.cs156.team02.controllers;

import edu.ucsb.cs156.team02.entities.UCSBSubject;
import edu.ucsb.cs156.team02.entities.User;
import edu.ucsb.cs156.team02.models.CurrentUser;
import edu.ucsb.cs156.team02.repositories.UCSBSubjectRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.oauth2.sdk.Response;

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

@Api(description = "UCSBSubjects")
@RequestMapping("/api/UCSBSubjects")
@RestController
@Slf4j
public class UCSBSubjectController extends ApiController {
    
    // Helper class for dealing with errors with GET, PUT, and DELETE endpoints for a single item
    public class UCSBSubjectOrError {
        Long id;
        UCSBSubject ucsbSubject;
        ResponseEntity<String> error;

        public UCSBSubjectOrError(Long id) {
            this.id = id;
        }
    }
    
    
    @Autowired
    private UCSBSubjectRepository ucsbSubjectRepository;

    @Autowired
    ObjectMapper mapper;

    @ApiOperation(value = "List all UCSB Subjects")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<UCSBSubject> allUsersSubjects() {
        loggingService.logMethod();
        Iterable<UCSBSubject> subjects = ucsbSubjectRepository.findAll();
        return subjects;
    }

    @ApiOperation(value = "Create a new UCSBSubject") 
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/post")
    public UCSBSubject postSubject(
            @ApiParam("Subject Code") @RequestParam String subjectCode,
            @ApiParam("Subject Translation") @RequestParam String subjectTranslation,
            @ApiParam("Department Code") @RequestParam String deptCode,
            @ApiParam("College Code: Only 'ENGR' or 'L&S' or 'UCSB'") @RequestParam String collegeCode,
            @ApiParam("Related Department Code") @RequestParam String relatedDeptCode,
            @ApiParam("Inactive") @RequestParam Boolean inactive) {
        loggingService.logMethod();
        CurrentUser currentUser = getCurrentUser();
        log.info("currentUser={}", currentUser);

        UCSBSubject subject = new UCSBSubject();
        subject.setSubjectCode(subjectCode);
        subject.setSubjectTranslation(subjectTranslation);
        subject.setDeptCode(deptCode);
        subject.setCollegeCode(collegeCode);
        subject.setRelatedDeptCode(relatedDeptCode);
        subject.setInactive(inactive);
        UCSBSubject savedSubject = ucsbSubjectRepository.save(subject);
        return savedSubject;
    }

    // Function that implements an endpoint to return JSON of the database record with a specific id
    // Returns an 400 and error message otherwise
    @ApiOperation(value = "Get a single UCSBSubject by ID")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public ResponseEntity<String> getSubjectByID(@ApiParam("ID") @RequestParam Long id) throws JsonProcessingException {
        
        loggingService.logMethod();
        UCSBSubjectOrError soe = new UCSBSubjectOrError(id);
        
        soe = doesUCSBSubjectExist(soe);
        if (soe.error != null) {
            return soe.error;
        }
        String body = mapper.writeValueAsString(soe.ucsbSubject);
        return ResponseEntity.ok().body(body);
    }

    //Function implements an endpoint in order to submit an UCSB subject based on specific id to the database record.
    @ApiOperation(value = "Update a single UCSBSubject via id")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping("")
    public ResponseEntity<String> putSubjectById(
            @ApiParam("id") @RequestParam Long id,
            @RequestBody @Valid UCSBSubject incomingUCSBSubject) throws JsonProcessingException {
        loggingService.logMethod();

        //CurrentUser currentUser = getCurrentUser();
        //User user = currentUser.getUser();

        UCSBSubjectOrError soe = new UCSBSubjectOrError(id);

        soe = doesUCSBSubjectExist(soe);
        if (soe.error != null) {
            return soe.error;
        }

        /*
        soe = doesTodoBelongToCurrentUser(soe);
        if (soe.error != null) {
            return soe.error;
        }
        */

        //incomingUCSBSubject.setUser(user);
        ucsbSubjectRepository.save(incomingUCSBSubject);

        String body = mapper.writeValueAsString(incomingUCSBSubject);
        return ResponseEntity.ok().body(body);
    }
    // soe.id is item being looked up
    // If UCSBSubject with id soe.id exists, it is copied to soe.UCSBSubject and toe.error is null
    // Otherwise, soe.error is the appropriate string to report the error condition that UCSBSubject with id soe.id does not exist
    
    public UCSBSubjectOrError doesUCSBSubjectExist(UCSBSubjectOrError soe) {

        Optional<UCSBSubject> optionalUCSBSubject = ucsbSubjectRepository.findById(soe.id);

        if (optionalUCSBSubject.isEmpty()) {
            soe.error = ResponseEntity
                    .badRequest()
                    .body(String.format("id %d not found", soe.id));
        } else {
            soe.ucsbSubject = optionalUCSBSubject.get();
        }
        return soe;
    }

    /**
     * Pre-conditions: soe.todo is non-null and refers to the todo with id soe.id,
     * and soe.error is null
     * 
     * Post-condition: if UCSBSubject belongs to current user, then error is still null.
     * Otherwise error is a suitable
     * return value.
     */
    /*
    public UCSBSubjectOrError doesTodoBelongToCurrentUser(UCSBSubjectOrError soe) {
        CurrentUser currentUser = getCurrentUser();
        log.info("currentUser={}", currentUser);

        Long currentUserId = currentUser.getUser().getId();
        Long ucsbUserId = soe.todo.getUser().getId();
        log.info("currentUserId={} todoUserId={}", currentUserId, ucsbUserId);

        if (ucsbUserId != currentUserId) {
            soe.error = ResponseEntity
                    .badRequest()
                    .body(String.format("todo with id %d not found", soe.id));
        }
        return soe;
    }
    */
}
