package hr.java.web.zadatakhardware.controller;

import hr.java.web.zadatakhardware.dto.HardwareDTO;
import hr.java.web.zadatakhardware.service.HardwareService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
