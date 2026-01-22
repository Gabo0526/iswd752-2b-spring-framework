package epn.edu.ec.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import epn.edu.ec.model.cake.UpdateCakeRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import epn.edu.ec.exception.CakeNotFoundException;
import epn.edu.ec.model.cake.CakeResponse;
import epn.edu.ec.model.cake.CakesResponse;
import epn.edu.ec.model.cake.CreateCakeRequest;
import epn.edu.ec.repository.CakeRepository;
import epn.edu.ec.repository.model.Cake;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
public class CakeServiceTest {

    @Mock
    private CakeRepository cakeRepository;

    @InjectMocks
    private CakeService cakeService;

    private Cake cakeA;
    private Cake cakeB;

    @BeforeEach
    public void setUp() {
        cakeA = Cake.builder()
                .id(1L)
                .title("Chocolate Cake")
                .description("Delicious chocolate cake")
                .build();
        cakeB = Cake.builder()
                .id(2L)
                .title("Vanilla Cake")
                .description("Delicius Vanilla cake")
                .build();
    }

    @Test
    public void getCakes_ShouldReturnAllCakesSortedByTitle() {
        //ARRANGE
        List<Cake> cakes = Arrays.asList(cakeB, cakeA);
        when(cakeRepository.findAll()).thenReturn(cakes);

        //ACT
        CakesResponse cakesResponse = cakeService.getCakes();

        //ASSERT
        assertNotNull(cakesResponse);
        assertEquals(2, cakesResponse.getCakes().size());

        assertEquals("Chocolate Cake", cakesResponse.getCakes().get(0).getTitle());
        assertEquals("Vanilla Cake", cakesResponse.getCakes().get(1).getTitle());

        verify(cakeRepository, times(1)).findAll();

    }

    @Test
    public void getCakeById_ShouldReturnCake_WhenCakeExists() {
        //ARRANGE
        when(cakeRepository.findById(anyLong())).thenReturn(Optional.of(cakeA));

        //ACT
        CakeResponse cakeResponse = cakeService.getCakeById(2L);
        //ASSERT
        assertNotNull(cakeResponse);
        assertEquals("Chocolate Cake", cakeResponse.getTitle());
        assertEquals(1L, cakeResponse.getId());
    }

    @Test
    public void getCakeById_SholdThrowException_WhenCakeDoesNotExist() {
        //ARRANGE
        long nonExistentCakeId = 999L;
        when(cakeRepository.findById(nonExistentCakeId)).thenReturn(Optional.empty());
        //ACT & ASSERT
        assertThrows(CakeNotFoundException.class, () -> {
            cakeService.getCakeById(nonExistentCakeId);
        });
    }

    @Test
    public void createCake_shouldSaveAndReturnNewCake() {
        //ARRANGE
        Cake newCake = Cake.builder()
                .id(3L)
                .title("Red Velvet Cake")
                .description("Delicious red velvet cake")
                .build();
        when(cakeRepository.save(any(Cake.class))).thenReturn(newCake);

        //ACT
        CreateCakeRequest createCakeRequest = CreateCakeRequest.builder()
                .title("Red Velvet Cake")
                .description("Delicious red velvet cake")
                .build();
        CakeResponse cakeResponse = cakeService.createCake(createCakeRequest);

        //ASSERT
        assertNotNull(cakeResponse);
        assertEquals(3L, cakeResponse.getId());
        assertEquals("Red Velvet Cake", cakeResponse.getTitle());
    }

    @Test
    public void updateCake_ShouldUpdateExistingCake() {
        // ARRANGE
        long cakeId = 1L;

        UpdateCakeRequest updateCakeRequest = UpdateCakeRequest.builder()
                .title("Updated Chocolate Cake")
                .description("Updated description")
                .build();

        Cake updatedCake = Cake.builder()
                .id(cakeId)
                .title(updateCakeRequest.getTitle())
                .description(updateCakeRequest.getDescription())
                .build();

        when(cakeRepository.findById(cakeId)).thenReturn(Optional.of(cakeA));
        when(cakeRepository.save(any(Cake.class))).thenReturn(updatedCake);

        // ACT
        CakeResponse response = cakeService.updateCake(cakeId, updateCakeRequest);

        // ASSERT
        assertNotNull(response);
        assertEquals(cakeId, response.getId());
        assertEquals("Updated Chocolate Cake", response.getTitle());
        assertEquals("Updated description", response.getDescription());

        verify(cakeRepository, times(1)).findById(cakeId);
        verify(cakeRepository, times(1)).save(any(Cake.class));
    }

    @Test
    public void deleteCake_ShouldRemoveExistingCake() {
        // ARRANGE
        long cakeId = 1L;
        when(cakeRepository.findById(cakeId)).thenReturn(Optional.of(cakeA));

        // ACT
        cakeService.deleteCake(cakeId);

        // ASSERT
        verify(cakeRepository, times(1)).findById(cakeId);
        verify(cakeRepository, times(1)).delete(cakeA);
    }

    @Test
    public void deleteCake_ShouldThrowException_WhenCakeDoesNotExist() {
        // ARRANGE
        long nonExistentCakeId = 999L;
        when(cakeRepository.findById(nonExistentCakeId)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(CakeNotFoundException.class, () -> cakeService.deleteCake(nonExistentCakeId));

        verify(cakeRepository, times(1)).findById(nonExistentCakeId);
        verify(cakeRepository, times(0)).delete(any(Cake.class));
    }

}
