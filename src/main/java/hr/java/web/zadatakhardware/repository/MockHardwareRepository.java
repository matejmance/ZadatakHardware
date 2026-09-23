package hr.java.web.zadatakhardware.repository;

import hr.java.web.zadatakhardware.domain.Hardware;
import hr.java.web.zadatakhardware.domain.HardwareType;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MockHardwareRepository implements HardwareRepository {

    private static List<Hardware> hardwareList;

    static {
        hardwareList = new ArrayList<>();
        Hardware firstHardware = new Hardware(
                "intel NUC",
                "1111",
                new BigDecimal("240"),
                HardwareType.OTHER,
                1
        );
        Hardware secondHardware = new Hardware(
                "AMD Ryzen 7 7800X3D",
                "2222",
                new BigDecimal("380"),
                HardwareType.CPU,
                6
        );

        Hardware thirdHardware = new Hardware(
                "NVIDIA GeForce RTX 5070",
                "3333",
                new BigDecimal("650"),
                HardwareType.GPU,
                4
        );

        Hardware fourthHardware = new Hardware(
                "Kingston Fury 32GB DDR5",
                "4444",
                new BigDecimal("120"),
                HardwareType.RAM,
                12
        );
        hardwareList.add(firstHardware);
        hardwareList.add(secondHardware);
        hardwareList.add(thirdHardware);
        hardwareList.add(fourthHardware);
    }

    @Override
    public List<Hardware> getAllHardware() {
        return hardwareList;
    }

    @Override
    public List<Hardware> getHardwareByCode(String code) {
        return hardwareList.stream().filter(hardware -> hardware.getCode().equalsIgnoreCase(code)).toList();
    }

    @Override
    public void saveHardware(Hardware hardware) {
        hardwareList.add(hardware);
    }

    @Override
    public void updateHardware(String code, Hardware hardware) {
        hardwareList.stream()
                .filter(existingHardware -> existingHardware.getCode().equalsIgnoreCase(code))
                .findFirst()
                .ifPresent(existingHardware -> {
                    existingHardware.setName(hardware.getName());
                    existingHardware.setCode(hardware.getCode());
                    existingHardware.setPrice(hardware.getPrice());
                    existingHardware.setType(hardware.getType());
                    existingHardware.setStock(hardware.getStock());
                });

    }
    @Override
    public void deleteHardware(String code) {
        hardwareList.removeIf(hardware -> hardware.getCode().equalsIgnoreCase(code));
    }
}
