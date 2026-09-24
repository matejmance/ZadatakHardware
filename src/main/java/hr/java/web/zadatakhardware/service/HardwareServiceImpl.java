package hr.java.web.zadatakhardware.service;

import hr.java.web.zadatakhardware.domain.Hardware;
import hr.java.web.zadatakhardware.domain.HardwareType;
import hr.java.web.zadatakhardware.dto.HardwareDTO;
import hr.java.web.zadatakhardware.repository.HardwareRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class HardwareServiceImpl implements HardwareService {
    private HardwareRepository hardwareRepository;

    @Override
    public List<HardwareDTO> getAllHardware() {
        return hardwareRepository.getAllHardware()
                .stream()
                .map(this::convertHardwareToHardwareDTO)
                .toList();
    }

    @Override
    public List<HardwareDTO> getHardwareByCode(String code) {
        return hardwareRepository.getHardwareByCode(code)
                .stream()
                .map(this::convertHardwareToHardwareDTO)
                .toList();
    }


    private HardwareDTO convertHardwareToHardwareDTO(Hardware hardware) {
        return new HardwareDTO(
                hardware.getName(),
                hardware.getCode(),
                hardware.getPrice(),
                hardware.getType().name(),
                hardware.getStock()
        );
    }
    @Override
    public void saveHardware(HardwareDTO hardwareDTO) {
        Hardware hardware = convertHardwareDTOToHardware(hardwareDTO);
        hardwareRepository.saveHardware(hardware);
    }

    @Override
    public void updateHardware(String code, HardwareDTO hardwareDTO) {
        Hardware hardware = convertHardwareDTOToHardware(hardwareDTO);
        hardwareRepository.updateHardware(code, hardware);
    }

    private Hardware convertHardwareDTOToHardware(HardwareDTO hardwareDTO) {
        return new Hardware(
                null,
                hardwareDTO.getName(),
                hardwareDTO.getCode(),
                hardwareDTO.getPrice(),
                HardwareType.valueOf(hardwareDTO.getType().toUpperCase()),
                hardwareDTO.getStock()
        );
    }
    @Override
    public void deleteHardware(String code) {
        hardwareRepository.deleteHardware(code);
    }

}
