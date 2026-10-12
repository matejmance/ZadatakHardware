package hr.java.web.zadatakhardware.service;

import hr.java.web.zadatakhardware.domain.Hardware;
import hr.java.web.zadatakhardware.dto.HardwareDTO;

import java.util.List;

public interface HardwareService {
    List<HardwareDTO> getAllHardware();
    List<HardwareDTO> getHardwareByCode(String code);

    HardwareDTO saveHardware(HardwareDTO hardwareDTO);
    HardwareDTO updateHardware(String code, HardwareDTO hardwareDTO);
    void deleteHardware(String code);
}
