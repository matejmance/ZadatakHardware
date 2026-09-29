package hr.java.web.zadatakhardware.repository;

import hr.java.web.zadatakhardware.domain.Hardware;
import hr.java.web.zadatakhardware.domain.HardwareType;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Primary
@Repository
@AllArgsConstructor
public class JdbcHardwareRepository implements HardwareRepository {
    private JdbcTemplate jdbcTemplate;


    @Override
    public List<Hardware> getAllHardware() {
        String sql = "SELECT Hardware.* , Type.name AS type FROM Hardware JOIN Type ON Hardware.typeId = Type.id";
        return jdbcTemplate.query(sql,new HardwareMapper());
    }

    @Override
    public List<Hardware> getHardwareByCode(String code) {
        String sql = """
            SELECT Hardware.*, Type.name AS type FROM Hardware
            JOIN Type ON Hardware.typeId = Type.id
            WHERE Hardware.code = ?
            """;
        return jdbcTemplate.query(sql,new HardwareMapper(),code);
    }

    @Override
    public void saveHardware(Hardware hardware) {
        Integer typeId = jdbcTemplate.queryForObject("SELECT id FROM Type WHERE name=?",Integer.class,hardware.getType().name());

        String sql = "INSERT INTO Hardware(name,code,price,typeId,stock) VALUES(?,?,?,?,?)";

        jdbcTemplate.update(
                sql,
                hardware.getName(),
                hardware.getCode(),
                hardware.getPrice(),
                typeId,
                hardware.getStock()
        );



    }

    @Override
    public void updateHardware(String code, Hardware hardware) {
        Integer typeId = jdbcTemplate.queryForObject("SELECT id FROM Type WHERE name=?",Integer.class,hardware.getType().name());

        String sql = """
            UPDATE Hardware
            SET name = ?, code = ?, price = ?, typeId = ?, stock = ?
            WHERE code = ?
            """;



        int rows = jdbcTemplate.update(
                sql,
                hardware.getName(),
                hardware.getCode(),
                hardware.getPrice(),
                typeId,
                hardware.getStock(),
                code
        );



    }

    @Override
    public void deleteHardware(String code) {
        String sql = " DELETE FROM Hardware WHERE code = ? ";

        jdbcTemplate.update(sql, code);

    }

    private static class HardwareMapper implements RowMapper<Hardware> {

        @Override
        public Hardware mapRow(ResultSet rs, int rowNum) throws SQLException {
            Hardware hardware = new Hardware();

            hardware.setId(rs.getLong("ID"));
            hardware.setName(rs.getString("NAME"));
            hardware.setCode(rs.getString("CODE"));
            hardware.setPrice(rs.getBigDecimal("PRICE"));
            hardware.setStock(rs.getInt("STOCK"));
            hardware.setType(HardwareType.valueOf(rs.getString("TYPE")));

            return hardware;
        }
    }
}
