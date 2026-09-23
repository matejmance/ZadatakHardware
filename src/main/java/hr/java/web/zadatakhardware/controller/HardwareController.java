package hr.java.web.zadatakhardware.controller;

import hr.java.web.zadatakhardware.dto.HardwareDTO;
import hr.java.web.zadatakhardware.service.HardwareService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        return hardwareService.getHardwareByCode(code);
    }
    @PostMapping
    public void saveHardware(@Valid @RequestBody HardwareDTO hardwareDTO){
        hardwareService.saveHardware(hardwareDTO);
    }

    @PutMapping("/{code}")
    public void updateHardware(@PathVariable String code, @Valid @RequestBody HardwareDTO hardwareDTO) {
        hardwareService.updateHardware(code, hardwareDTO);
    }
    @DeleteMapping("/{code}")
    public void deleteHardware(@PathVariable String code) {
        hardwareService.deleteHardware(code);
    }
}
