package hr.java.web.zadatakhardware.controller;

import hr.java.web.zadatakhardware.dto.HardwareDTO;
import hr.java.web.zadatakhardware.service.HardwareService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.ResponseEntity;
@RestController
@RequestMapping("/hardware")
@AllArgsConstructor
public class HardwareController {
    private HardwareService hardwareService;
    @GetMapping
    public List<HardwareDTO> getAllHardware() {
        return hardwareService.getAllHardware();
    }
    @GetMapping("/{code}")
    public List<HardwareDTO> getHardwareByCode(@PathVariable String code) {
        List<HardwareDTO> hardware = hardwareService.getHardwareByCode(code);

        if (hardware.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Hardver s kodom " + code + " nije pronađen"
            );
        }

        return hardware;
    }
    @PostMapping
    public ResponseEntity<HardwareDTO> saveHardware(
            @Valid @RequestBody HardwareDTO hardwareDTO) {

        HardwareDTO savedHardware = hardwareService.saveHardware(hardwareDTO);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedHardware);
    }
    @PutMapping("/{code}")
    public ResponseEntity<HardwareDTO> updateHardware(
            @PathVariable String code,
            @Valid @RequestBody HardwareDTO hardwareDTO) {

        HardwareDTO updatedHardware =
                hardwareService.updateHardware(code, hardwareDTO);

        return ResponseEntity.ok(updatedHardware);
    }
    @DeleteMapping("/{code}")
    public ResponseEntity<Void> deleteHardware(@PathVariable String code) {
        hardwareService.deleteHardware(code);

        return ResponseEntity.noContent().build();
    }
}
