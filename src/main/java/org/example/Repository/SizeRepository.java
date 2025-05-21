package org.example.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.example.entity.Size;

public interface SizeRepository extends JpaRepository<Size, Long> {
}
