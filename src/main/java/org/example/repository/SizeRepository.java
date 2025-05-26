package org.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.entity.Size;
import java.util.Optional;
import java.util.List;

public interface SizeRepository extends JpaRepository<Size, Long> {
    Optional<Size> findBySizeNumber(Integer sizeNumber);
    List<Size> findBySizeNumberGreaterThan(Integer sizeNumber);
    List<Size> findBySizeNumberLessThan(Integer sizeNumber);
    boolean existsBySizeNumber(Integer sizeNumber);
}
