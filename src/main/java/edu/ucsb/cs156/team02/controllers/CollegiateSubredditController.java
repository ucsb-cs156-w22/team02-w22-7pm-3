package edu.ucsb.cs156.team02.controllers;

import edu.ucsb.cs156.team02.entities.CollegiateSubreddit;
import edu.ucsb.cs156.team02.entities.User;
import edu.ucsb.cs156.team02.models.CurrentUser;
import edu.ucsb.cs156.team02.repositories.CollegiateSubredditRepository;
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

@Api(description = "Collegiate Subreddits")
@RequestMapping("/api/collegiate_subreddits")
@RestController
@Slf4j
public class CollegiateSubredditController extends ApiController{

    public class RecordOrError {
        CollegiateSubreddit record;
        ResponseEntity<String> error;
    }

    @Autowired
    CollegiateSubredditRepository collegiateSubredditRepository;

    @Autowired
    ObjectMapper mapper;

    @ApiOperation(value = "List all collegiate subreddits.")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<CollegiateSubreddit> allCollegiateSubReddits() {
        loggingService.logMethod();
        Iterable<CollegiateSubreddit> subreddits = collegiateSubredditRepository.findAll();
        return subreddits;
    }

    @ApiOperation(value = "Create a new CollegiateSubreddit.")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/post")
    public CollegiateSubreddit postCollegiateSubreddit(
            @ApiParam("Enter name:") @RequestParam String name,
            @ApiParam("Enter location:") @RequestParam String location,
            @ApiParam("Enter subreddit:") @RequestParam String subreddit) {
        loggingService.logMethod();
        CurrentUser currentUser = getCurrentUser();
        log.info("currentUser={}", currentUser);

        CollegiateSubreddit collegiateSubreddit = new CollegiateSubreddit();
        collegiateSubreddit.setName(name);
        collegiateSubreddit.setLocation(location);
        collegiateSubreddit.setSubreddit(subreddit);
        CollegiateSubreddit savedCollegiateSubreddit = collegiateSubredditRepository.save(collegiateSubreddit);
        return savedCollegiateSubreddit;
    }

    @ApiOperation(value = "Returns the database record with id 123 if it exists, or a error message if it does not.")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public ResponseEntity<String> getRecord123() throws JsonProcessingException {
        loggingService.logMethod();

        RecordOrError roe = new RecordOrError();

        roe = doesRecord123Exist(roe);
        if (roe.error != null) {
            return roe.error;
        }

        String body = mapper.writeValueAsString(roe.record);
        return ResponseEntity.ok().body(body);
    }

    public RecordOrError doesRecord123Exist(RecordOrError roe) {
        Optional<CollegiateSubreddit> optionalRecord = collegiateSubredditRepository.findById(123L);

        if(optionalRecord.isEmpty()) {
            roe.error = ResponseEntity
            .badRequest()
            .body(String.format("id 123 not found"));
        }else{
            roe.record = optionalRecord.get();
        }
        return roe;
    }

}
