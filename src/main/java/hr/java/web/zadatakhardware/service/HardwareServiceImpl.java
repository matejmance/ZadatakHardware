package hr.java.web.zadatakhardware.service;

import hr.java.web.zadatakhardware.domain.Hardware;

import hr.java.web.zadatakhardware.domain.Type;
import hr.java.web.zadatakhardware.dto.HardwareDTO;

import hr.java.web.zadatakhardware.repository.SpringDataHardwareRepository;
import hr.java.web.zadatakhardware.repository.SpringDataTypeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.boot.jdbc.DataSourceBuilder.findType;

@Service
@AllArgsConstructor
public class HardwareServiceImpl implements HardwareService {


    private SpringDataHardwareRepository hardwareRepository;
    private SpringDataTypeRepository typeRepository;

    @Override
    public List<HardwareDTO> getAllHardware() {
        return hardwareRepository.findAll()
                .stream()
                .map(this::convertHardwareToHardwareDTO)
                .toList();
    }

    @Override
    public List<HardwareDTO> getHardwareByCode(String code) {
        return hardwareRepository.findByCode(code)
                .stream()
                .map(this::convertHardwareToHardwareDTO)
                .toList();
    }


    private HardwareDTO convertHardwareToHardwareDTO(Hardware hardware) {
        return new HardwareDTO(
                hardware.getName(),
                hardware.getCode(),
                hardware.getPrice(),
                hardware.getType().getName(),
                hardware.getStock()
        );
    }
    @Override
    public void saveHardware(HardwareDTO hardwareDTO) {
        Hardware hardware = convertHardwareDTOToHardware(hardwareDTO);
        hardwareRepository.save(hardware);
    }

    @Override
    public void updateHardware(String code, HardwareDTO hardwareDTO) {
        hardwareRepository.findByCode(code)
                .stream()
                .findFirst()
                .ifPresent(existingHardware -> {
                    existingHardware.setName(hardwareDTO.getName());
                    existingHardware.setCode(hardwareDTO.getCode());
                    existingHardware.setPrice(hardwareDTO.getPrice());
                    existingHardware.setType(findType(hardwareDTO.getType()));
                    existingHardware.setStock(hardwareDTO.getStock());

                    hardwareRepository.save(existingHardware);
                });
    }

    private Hardware convertHardwareDTOToHardware(HardwareDTO hardwareDTO) {
        return new Hardware(
                null,
                hardwareDTO.getName(),
                hardwareDTO.getCode(),
                hardwareDTO.getPrice(),
                findType(hardwareDTO.getType()),
                hardwareDTO.getStock()
        );
    }
    @Override
    public void deleteHardware(String code) {
        hardwareRepository.deleteAll(hardwareRepository.findByCode(code));
    }

    private Type findType(String typeName) {
        return typeRepository.findByNameIgnoreCase(typeName).orElseThrow(() -> new IllegalArgumentException("type not found"));
    }

}
