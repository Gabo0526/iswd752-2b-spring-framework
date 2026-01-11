package epn.edu.ec.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

import epn.edu.ec.model.cake.CakeResponse;
import epn.edu.ec.model.cake.CakesResponse;
import epn.edu.ec.model.cake.CreateCakeRequest;
import epn.edu.ec.service.CakeService;

@WebMvcTest(controllers = CakeController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@ActiveProfiles("test")
public class CakeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean  // -> @Mock
    private CakeService cakeService;

    private final long cakeId = 1;
    private final CakeResponse mockCakeResponse = new CakeResponse(
            cakeId, "Mock Cake", "Mock Cake Description"
    );

    @Test
    public void getCakes_shouldReturnListOfCakes() throws Exception {
        //ARRAGE
        //Codigo Similar
        //List<CakeResponse> cakeList = new ArrayList<>();
        //cakeList.add(mockCakeResponse);
        CakesResponse cakesResponse = new CakesResponse(List.of(mockCakeResponse));
        when(cakeService.getCakes()).thenReturn(cakesResponse);

        //ACT
        ResultActions result = mockMvc.perform(get("/cakes")
                .contentType("application/json"));

        //ASSERT
        result.andExpect(status().isOk());
        result.andExpect(content().contentType("application/json"));
        result.andExpect(content().json(objectMapper.writeValueAsString(cakesResponse)));

        //System.out.println(result.andReturn().getResponse().getContentAsString());

        verify(cakeService, times(1)).getCakes();
    }

    public void createCake_shouldCreateCake() throws Exception{
        //ARRANGE
        //Request
        CreateCakeRequest createCakeRequest =
                CreateCakeRequest.builder().title("New Cake")
                        .description("New Cake Description").build();
        //Response
        CakeResponse cakeResponse =
                CakeResponse.builder().id(2l)
                        .title("New Cake")
                        .description("New Cake Description").build();

        when(cakeService.createCake(createCakeRequest)).thenReturn(cakeResponse);

        //ACT
        ResultActions result = mockMvc.perform(post("/cakes")
                .content("application/json")
                .content(objectMapper.writeValueAsString(createCakeRequest)));

        //ASSERT
        result.andExpect(status().isCreated());

    }


}
