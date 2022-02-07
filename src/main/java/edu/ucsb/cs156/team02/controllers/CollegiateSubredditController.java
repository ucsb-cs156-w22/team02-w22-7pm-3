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

    public class CollegiateSubredditOrError {
        Long id;
        CollegiateSubreddit collegiateSubreddit;
        ResponseEntity<String> error;

        public CollegiateSubredditOrError(Long id) { this.id = id; }
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


    /**
     * Pre-conditions: toe.id is value to look up, toe.todo and toe.error are null
     * 
     * Post-condition: if todo with id toe.id exists, toe.todo now refers to it, and
     * error is null.
     * Otherwise, todo with id toe.id does not exist, and error is a suitable return
     * value to
     * report this error condition.
     */
    public CollegiateSubredditOrError doesCollegiateSubredditExist(CollegiateSubredditOrError toe) {

        Optional<CollegiateSubreddit> optionalCollegiateSubreddit = collegiateSubredditRepository.findById(toe.id);

        if (optionalCollegiateSubreddit.isEmpty()) {
            toe.error = ResponseEntity
                    .badRequest()
                    .body(String.format("todo with id %d not found", toe.id));
        } else {
            toe.collegiateSubreddit = optionalCollegiateSubreddit.get();
        }
        return toe;
    }

    /**
     * Pre-conditions: toe.todo is non-null and refers to the todo with id toe.id,
     * and toe.error is null
     * 
     * Post-condition: if todo belongs to current user, then error is still null.
     * Otherwise error is a suitable
     * return value.
     */
    public CollegiateSubredditOrError doesCollegiateSubredditBelongToCurrentUser(CollegiateSubredditOrError toe) {
        CurrentUser currentUser = getCurrentUser();
        log.info("currentUser={}", currentUser);

        Long currentUserId = currentUser.getUser().getId();
        Long collegiateSubredditUserId = toe.collegiateSubreddit.getId();
        log.info("currentUserId={} todoUserId={}", currentUserId, collegiateSubredditUserId);

        if (!collegiateSubredditUserId.equals(currentUserId)) {
            toe.error = ResponseEntity
                    .badRequest()
                    .body(String.format("collegiate subreddit with id %d not found", toe.id));
        }
        return toe;
    }
}
