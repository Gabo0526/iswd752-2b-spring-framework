package epn.edu.ec.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import epn.edu.ec.exception.CakeNotFoundException;
import epn.edu.ec.model.cake.UpdateCakeRequest;
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

    @Test
    public void createCake_shouldCreateCake() throws Exception {
        // ARRANGE
        CreateCakeRequest createCakeRequest = CreateCakeRequest.builder()
                .title("New Cake")
                .description("New Cake Description")
                .build();

        CakeResponse cakeResponse = CakeResponse.builder()
                .id(2L)
                .title("New Cake")
                .description("New Cake Description")
                .build();

        when(cakeService.createCake(any(CreateCakeRequest.class))).thenReturn(cakeResponse);

        // ACT
        ResultActions result = mockMvc.perform(post("/cakes")
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createCakeRequest)));

        // ASSERT
        result.andExpect(status().isCreated());
        result.andExpect(content().contentType(APPLICATION_JSON));
        result.andExpect(content().json(objectMapper.writeValueAsString(cakeResponse)));

        verify(cakeService, times(1)).createCake(any(CreateCakeRequest.class));
    }

    @Test
    public void getCakes_shouldReturnEmptyList() throws Exception {
        // ARRANGE
        CakesResponse emptyResponse = new CakesResponse(List.of());
        when(cakeService.getCakes()).thenReturn(emptyResponse);

        // ACT
        ResultActions result = mockMvc.perform(get("/cakes")
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON));

        // ASSERT
        result.andExpect(status().isOk());
        result.andExpect(content().contentType(APPLICATION_JSON));
        result.andExpect(content().json(objectMapper.writeValueAsString(emptyResponse)));

        verify(cakeService, times(1)).getCakes();
    }

    @Test
    public void getCakeById_shouldReturnCake() throws Exception {
        // ARRANGE
        when(cakeService.getCakeById(cakeId)).thenReturn(mockCakeResponse);

        // ACT
        ResultActions result = mockMvc.perform(get("/cakes/{id}", cakeId)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON));

        // ASSERT
        result.andExpect(status().isOk());
        result.andExpect(content().contentType(APPLICATION_JSON));
        result.andExpect(content().json(objectMapper.writeValueAsString(mockCakeResponse)));

        verify(cakeService, times(1)).getCakeById(cakeId);
    }

    @Test
    public void getCakeById_shouldReturnNotFound() throws Exception {
        // ARRANGE
        when(cakeService.getCakeById(cakeId)).thenThrow(new CakeNotFoundException());

        // ACT
        ResultActions result = mockMvc.perform(get("/cakes/{id}", cakeId)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON));

        // ASSERT
        result.andExpect(status().isNotFound());
        verify(cakeService, times(1)).getCakeById(cakeId);
    }

    @Test
    public void updateCake_shouldUpdateCake() throws Exception {
        // ARRANGE
        UpdateCakeRequest updateCakeRequest = UpdateCakeRequest.builder()
                .title("Updated Cake")
                .description("Updated Description")
                .build();

        // Ojo: el controller ignora el retorno, pero el service lo retorna (entonces lo mockeamos igual)
        CakeResponse updatedResponse = new CakeResponse(cakeId, "Updated Cake", "Updated Description");

        when(cakeService.updateCake(eq(cakeId), any(UpdateCakeRequest.class))).thenReturn(updatedResponse);

        // ACT
        ResultActions result = mockMvc.perform(put("/cakes/{id}", cakeId)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateCakeRequest)));

        // ASSERT
        result.andExpect(status().isNoContent());
        verify(cakeService, times(1)).updateCake(eq(cakeId), any(UpdateCakeRequest.class));
    }

    @Test
    public void updateCake_shouldReturnNotFound() throws Exception {
        // ARRANGE
        UpdateCakeRequest updateCakeRequest = UpdateCakeRequest.builder()
                .title("Updated Cake")
                .description("Updated Description")
                .build();

        when(cakeService.updateCake(eq(cakeId), any(UpdateCakeRequest.class)))
                .thenThrow(new CakeNotFoundException());

        // ACT
        ResultActions result = mockMvc.perform(put("/cakes/{id}", cakeId)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateCakeRequest)));

        // ASSERT
        result.andExpect(status().isNotFound());
        verify(cakeService, times(1)).updateCake(eq(cakeId), any(UpdateCakeRequest.class));
    }

    @Test
    public void deleteCake_shouldDeleteCake() throws Exception {
        // ARRANGE
        doNothing().when(cakeService).deleteCake(cakeId);

        // ACT
        ResultActions result = mockMvc.perform(delete("/cakes/{id}", cakeId));

        // ASSERT
        result.andExpect(status().isNoContent());
        verify(cakeService, times(1)).deleteCake(cakeId);
    }

}
