package hr.java.web.zadatakhardware.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HardwareDTO {
    @NotBlank
    private String name;
    @NotBlank
    private String code;
    @NotNull
    @Positive
    private BigDecimal price;

    @NotBlank
    private String type;
    @NotNull
    @PositiveOrZero
    private Integer stock;
}
