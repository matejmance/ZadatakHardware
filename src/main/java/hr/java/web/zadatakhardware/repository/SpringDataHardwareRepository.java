package hr.java.web.zadatakhardware.repository;

import hr.java.web.zadatakhardware.domain.Hardware;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataHardwareRepository extends JpaRepository<Hardware, Long> {
    List<Hardware> findByCode(String code);
}