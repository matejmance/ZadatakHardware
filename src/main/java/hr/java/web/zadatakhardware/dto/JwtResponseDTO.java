package hr.java.web.zadatakhardware.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtResponseDTO {

    private final String accessToken;
    private final String token;
}