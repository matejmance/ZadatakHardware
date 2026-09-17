package hr.java.web.zadatakhardware.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HardwareDTO {
    private String name;
    private String code;
    private BigDecimal price;
    private String type;
    private Integer stock;
}
