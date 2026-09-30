package hr.java.web.zadatakhardware.repository;

import hr.java.web.zadatakhardware.domain.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpringDataTypeRepository extends JpaRepository<Type, Integer> {

    Optional<Type> findByNameIgnoreCase(String name);
}