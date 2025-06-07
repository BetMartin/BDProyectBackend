package org.example.repository;

import org.example.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.username = :username")
    Optional<User> findByUsername(@Param("username") String username);

    // Modificamos esta consulta para solo seleccionar los campos necesarios sin usar constructor
    @Query("SELECT DISTINCT u FROM User u WHERE u.username = :username")
    Optional<User> findUserForLogin(@Param("username") String username);

    boolean existsByUsername(String username);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.person p LEFT JOIN FETCH p.phone LEFT JOIN FETCH p.address LEFT JOIN FETCH p.rol WHERE u.user_id = :id")
    Optional<User> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.person p LEFT JOIN FETCH p.phone LEFT JOIN FETCH p.address LEFT JOIN FETCH p.rol")
    List<User> findAllWithDetails();

}