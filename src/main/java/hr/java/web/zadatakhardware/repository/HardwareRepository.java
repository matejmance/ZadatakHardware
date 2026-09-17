package hr.java.web.zadatakhardware.repository;

import hr.java.web.zadatakhardware.domain.Hardware;

import java.util.List;

public interface HardwareRepository {
    List<Hardware> getAllHardware();
    List<Hardware> getHardwareByCode(String code);
}
