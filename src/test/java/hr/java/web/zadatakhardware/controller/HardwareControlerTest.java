package hr.java.web.zadatakhardware.controller;

import hr.java.web.zadatakhardware.dto.HardwareDTO;
import hr.java.web.zadatakhardware.service.HardwareService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HardwareControlerTest {

    @Mock
    private HardwareService hardwareService;

    @InjectMocks
    private HardwareController hardwareController;

    @Test
    void testGetAll() {
        // ARRANGE
        HardwareDTO cpu = new HardwareDTO(
                "AMD Ryzen 7 7800X3D",
                "CPU-1",
                new BigDecimal("380.00"),
                "CPU",
                10
        );

        HardwareDTO gpu = new HardwareDTO(
                "NVIDIA GeForce RTX 5070",
                "GPU-1",
                new BigDecimal("650.00"),
                "GPU",
                5
        );

        List<HardwareDTO> expectedHardware = List.of(cpu, gpu);

        when(hardwareService.getAllHardware())
                .thenReturn(expectedHardware);

        // ACT
        List<HardwareDTO> result = hardwareController.getAllHardware();

        // ASSERT
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedHardware, result);

        verify(hardwareService, times(1)).getAllHardware();
        verifyNoMoreInteractions(hardwareService);
    }

    @Test
    void testGetByCode_Found() {
        // ARRANGE
        String code = "CPU-1";

        HardwareDTO cpu = new HardwareDTO(
                "AMD Ryzen 7 7800X3D",
                code,
                new BigDecimal("380.00"),
                "CPU",
                10
        );

        when(hardwareService.getHardwareByCode(code))
                .thenReturn(List.of(cpu));

        // ACT
        List<HardwareDTO> result =
                hardwareController.getHardwareByCode(code);

        // ASSERT
        assertEquals(1, result.size());
        assertEquals(cpu, result.get(0));

        verify(hardwareService, times(1)).getHardwareByCode(code);
        verifyNoMoreInteractions(hardwareService);
    }

    @Test
    void testGetByCode_NotFound() {
        // ARRANGE
        String code = "NEPOSTOJECI";

        when(hardwareService.getHardwareByCode(code))
                .thenReturn(List.of());

        // ACT + ASSERT
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> hardwareController.getHardwareByCode(code)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

        verify(hardwareService, times(1)).getHardwareByCode(code);
        verifyNoMoreInteractions(hardwareService);
    }

    @Test
    void testSave_Success() {
        // ARRANGE
        HardwareDTO hardware = new HardwareDTO(
                "AMD Ryzen 7 7800X3D",
                "CPU-1",
                new BigDecimal("380.00"),
                "CPU",
                10
        );

        when(hardwareService.saveHardware(hardware))
                .thenReturn(hardware);

        // ACT
        ResponseEntity<HardwareDTO> response =
                hardwareController.saveHardware(hardware);

        // ASSERT
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(hardware, response.getBody());

        verify(hardwareService, times(1)).saveHardware(hardware);
        verifyNoMoreInteractions(hardwareService);
    }

    @Test
    void testSave_Conflict() {
        // ARRANGE
        HardwareDTO hardware = new HardwareDTO(
                "AMD Ryzen 7 7800X3D",
                "CPU-1",
                new BigDecimal("380.00"),
                "CPU",
                10
        );

        when(hardwareService.saveHardware(hardware))
                .thenThrow(new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Hardver s tim kodom već postoji"
                ));

        // ACT + ASSERT
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> hardwareController.saveHardware(hardware)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());

        verify(hardwareService, times(1)).saveHardware(hardware);
        verifyNoMoreInteractions(hardwareService);
    }

    @Test
    void testUpdate_Success() {
        // ARRANGE
        String code = "CPU-1";

        HardwareDTO hardware = new HardwareDTO(
                "AMD Ryzen 7 7800X3D - ažurirano",
                code,
                new BigDecimal("350.00"),
                "CPU",
                8
        );

        when(hardwareService.updateHardware(code, hardware))
                .thenReturn(hardware);

        // ACT
        ResponseEntity<HardwareDTO> response =
                hardwareController.updateHardware(code, hardware);

        // ASSERT
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(hardware, response.getBody());

        verify(hardwareService, times(1))
                .updateHardware(code, hardware);

        verifyNoMoreInteractions(hardwareService);
    }

    @Test
    void testUpdate_NotFound() {
        // ARRANGE
        String code = "NEPOSTOJECI";

        HardwareDTO hardware = new HardwareDTO(
                "AMD Ryzen 7 7800X3D",
                code,
                new BigDecimal("350.00"),
                "CPU",
                8
        );

        when(hardwareService.updateHardware(code, hardware))
                .thenThrow(new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Hardver s kodom " + code + " nije pronađen"
                ));

        // ACT + ASSERT
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> hardwareController.updateHardware(code, hardware)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

        verify(hardwareService, times(1))
                .updateHardware(code, hardware);

        verifyNoMoreInteractions(hardwareService);
    }

    @Test
    void testDelete() {
        // ARRANGE
        String code = "CPU-1";

        // ACT
        ResponseEntity<Void> response =
                hardwareController.deleteHardware(code);

        // ASSERT
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        verify(hardwareService, times(1)).deleteHardware(code);
        verifyNoMoreInteractions(hardwareService);
    }
}