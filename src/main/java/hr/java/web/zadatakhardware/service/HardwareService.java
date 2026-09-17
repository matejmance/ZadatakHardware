package hr.java.web.zadatakhardware.service;

import hr.java.web.zadatakhardware.domain.Hardware;
import hr.java.web.zadatakhardware.dto.HardwareDTO;

import java.util.List;

public interface HardwareService {
    List<HardwareDTO> getAllHardware();
    List<HardwareDTO> getHardwareByCode(String code);
}
