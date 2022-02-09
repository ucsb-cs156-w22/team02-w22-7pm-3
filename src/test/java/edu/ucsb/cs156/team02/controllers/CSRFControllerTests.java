package edu.ucsb.cs156.team02.controllers;

import edu.ucsb.cs156.team02.ControllerTestCase;
import edu.ucsb.cs156.team02.repositories.UserRepository;
import edu.ucsb.cs156.team02.testconfig.TestConfig;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("development")
@WebMvcTest(controllers = CSRFController.class)
@Import(TestConfig.class)
public class CSRFControllerTests extends ControllerTestCase {

  @MockBean
  UserRepository userRepository;

  @Test
  public void csrf_returns_ok() throws Exception {
      mockMvc.perform(get("/csrf"))
              .andExpect(status().isOk());
  }

}
