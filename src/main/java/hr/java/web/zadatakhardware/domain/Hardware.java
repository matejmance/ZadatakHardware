package hr.java.web.zadatakhardware.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Hardware {
    private String name;
    private String code;
    private BigDecimal price;
    private HardwareType type;
    private Integer stock;


}
