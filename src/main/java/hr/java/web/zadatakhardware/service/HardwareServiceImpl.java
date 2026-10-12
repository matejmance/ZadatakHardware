package hr.java.web.zadatakhardware.service;

import hr.java.web.zadatakhardware.domain.Hardware;

import hr.java.web.zadatakhardware.domain.Type;
import hr.java.web.zadatakhardware.dto.HardwareDTO;

import hr.java.web.zadatakhardware.repository.SpringDataHardwareRepository;
import hr.java.web.zadatakhardware.repository.SpringDataTypeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.boot.jdbc.DataSourceBuilder.findType;
import org.springframework.data.domain.Sort;

@Service
@AllArgsConstructor
public class HardwareServiceImpl implements HardwareService {


    private SpringDataHardwareRepository hardwareRepository;
    private SpringDataTypeRepository typeRepository;

    @Override
    public List<HardwareDTO> getAllHardware() {
        return hardwareRepository.findAll(Sort.by("id"))
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
    public HardwareDTO saveHardware(HardwareDTO hardwareDTO) {
        if (!hardwareRepository.findByCode(hardwareDTO.getCode()).isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Hardver s tim kodom već postoji"
            );
        }

        Hardware hardware = convertHardwareDTOToHardware(hardwareDTO);
        Hardware savedHardware = hardwareRepository.save(hardware);

        return convertHardwareToHardwareDTO(savedHardware);
    }

    @Override
    public HardwareDTO updateHardware(String code, HardwareDTO hardwareDTO) {
        Hardware existingHardware = hardwareRepository.findByCode(code)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Hardver s kodom " + code + " nije pronađen"
                ));

        existingHardware.setName(hardwareDTO.getName());
        existingHardware.setCode(hardwareDTO.getCode());
        existingHardware.setPrice(hardwareDTO.getPrice());
        existingHardware.setType(findType(hardwareDTO.getType()));
        existingHardware.setStock(hardwareDTO.getStock());

        Hardware updatedHardware = hardwareRepository.save(existingHardware);

        return convertHardwareToHardwareDTO(updatedHardware);
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
