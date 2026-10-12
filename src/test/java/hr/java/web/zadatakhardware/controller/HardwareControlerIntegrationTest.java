package hr.java.web.zadatakhardware.controller;

import hr.java.web.zadatakhardware.domain.Hardware;
import hr.java.web.zadatakhardware.domain.Type;
import hr.java.web.zadatakhardware.domain.UserInfo;
import hr.java.web.zadatakhardware.domain.UserRole;
import hr.java.web.zadatakhardware.service.JwtService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:hardware_test;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.sql.init.mode=never",
        "jwt.base64-secret=MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=",
        "jwt.access-token-validity-seconds=600",
        "jwt.refresh-token-validity-seconds=3600"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class HardwareControlerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String accessToken;

    @BeforeEach
    void setUp() {
        // Pripremamo tipove hardvera
        Type cpuType = new Type();
        cpuType.setId(1);
        cpuType.setName("CPU");
        entityManager.persist(cpuType);

        Type gpuType = new Type();
        gpuType.setId(2);
        gpuType.setName("GPU");
        entityManager.persist(gpuType);

        // Pripremamo administratorsku ulogu
        UserRole adminRole = new UserRole();
        adminRole.setId(1L);
        adminRole.setName("ROLE_ADMIN");
        entityManager.persist(adminRole);

        // Pripremamo korisnika kojem će pripadati JWT
        UserInfo admin = new UserInfo();
        admin.setUsername("test-admin");
        admin.setPassword(passwordEncoder.encode("test-password"));
        admin.getRoles().add(adminRole);
        entityManager.persist(admin);

        // Pripremamo dva zapisa o hardveru
        entityManager.persist(new Hardware(
                null,
                "AMD Ryzen 7 7800X3D",
                "CPU-1",
                new BigDecimal("380.00"),
                cpuType,
                10
        ));

        entityManager.persist(new Hardware(
                null,
                "NVIDIA GeForce RTX 5070",
                "GPU-1",
                new BigDecimal("650.00"),
                gpuType,
                5
        ));

        entityManager.flush();

        // Stvarni potpisani JWT za testnog administratora
        accessToken = jwtService.generateToken("test-admin");
    }

    @Test
    void testGetAll() throws Exception {
        mockMvc.perform(get("/hardware")
                        .header(
                                HttpHeaders.AUTHORIZATION,
                                "Bearer " + accessToken
                        )
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].code").value("CPU-1"))
                .andExpect(jsonPath("$[1].code").value("GPU-1"));
    }

    @Test
    void testGetByCode() throws Exception {
        mockMvc.perform(get("/hardware/{code}", "CPU-1")
                        .header(
                                HttpHeaders.AUTHORIZATION,
                                "Bearer " + accessToken
                        )
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].code").value("CPU-1"))
                .andExpect(jsonPath("$[0].name")
                        .value("AMD Ryzen 7 7800X3D"))
                .andExpect(jsonPath("$[0].price").value(380.0))
                .andExpect(jsonPath("$[0].type").value("CPU"))
                .andExpect(jsonPath("$[0].stock").value(10));
    }

    @Test
    void testSaveHardware() throws Exception {
        String requestBody = """
            {
                "name": "Intel Core i5-14600K",
                "code": "CPU-NEW",
                "price": 299.99,
                "type": "CPU",
                "stock": 7
            }
            """;

        mockMvc.perform(post("/hardware")
                        .header(
                                HttpHeaders.AUTHORIZATION,
                                "Bearer " + accessToken
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Intel Core i5-14600K"))
                .andExpect(jsonPath("$.code").value("CPU-NEW"))
                .andExpect(jsonPath("$.price").value(299.99))
                .andExpect(jsonPath("$.type").value("CPU"))
                .andExpect(jsonPath("$.stock").value(7));

        // Provjeravamo stvarno spremljene podatke
        entityManager.flush();
        entityManager.clear();

        Hardware savedHardware = entityManager.createQuery(
                        "select h from Hardware h where h.code = :code",
                        Hardware.class
                )
                .setParameter("code", "CPU-NEW")
                .getSingleResult();

        assertEquals("Intel Core i5-14600K", savedHardware.getName());

        assertEquals(
                0,
                new BigDecimal("299.99").compareTo(savedHardware.getPrice())
        );
    }

    @Test
    void testUpdateHardware() throws Exception {
        String requestBody = """
            {
                "name": "AMD Ryzen 7 7800X3D - ažurirano",
                "code": "CPU-1",
                "price": 350.00,
                "type": "CPU",
                "stock": 8
            }
            """;

        mockMvc.perform(put("/hardware/{code}", "CPU-1")
                        .header(
                                HttpHeaders.AUTHORIZATION,
                                "Bearer " + accessToken
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("CPU-1"))
                .andExpect(jsonPath("$.name")
                        .value("AMD Ryzen 7 7800X3D - ažurirano"))
                .andExpect(jsonPath("$.price").value(350.0))
                .andExpect(jsonPath("$.type").value("CPU"))
                .andExpect(jsonPath("$.stock").value(8));

        // Provjeravamo ažurirane vrijednosti u bazi
        entityManager.flush();
        entityManager.clear();

        Hardware updatedHardware = entityManager.createQuery(
                        "select h from Hardware h where h.code = :code",
                        Hardware.class
                )
                .setParameter("code", "CPU-1")
                .getSingleResult();

        assertEquals(
                "AMD Ryzen 7 7800X3D - ažurirano",
                updatedHardware.getName()
        );

        assertEquals(
                0,
                new BigDecimal("350.00").compareTo(updatedHardware.getPrice())
        );

        assertEquals(8, updatedHardware.getStock());
    }

    @Test
    void testDeleteHardware() throws Exception {
        mockMvc.perform(delete("/hardware/{code}", "CPU-1")
                        .header(
                                HttpHeaders.AUTHORIZATION,
                                "Bearer " + accessToken
                        ))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        entityManager.flush();
        entityManager.clear();

        Long count = entityManager.createQuery(
                        "select count(h) from Hardware h where h.code = :code",
                        Long.class
                )
                .setParameter("code", "CPU-1")
                .getSingleResult();

        assertEquals(0L, count.longValue());

        // Drugi hardver mora ostati dostupan
        mockMvc.perform(get("/hardware")
                        .header(
                                HttpHeaders.AUTHORIZATION,
                                "Bearer " + accessToken
                        )
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].code").value("GPU-1"));
    }
}