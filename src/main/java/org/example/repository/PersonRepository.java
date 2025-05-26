package org.example.repository;

import org.example.entity.Person;
import org.example.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long> {
    Optional<Person> findByDni(int dni);

    @Query("SELECT p FROM Person p " +
            "LEFT JOIN FETCH p.phone " +
            "LEFT JOIN FETCH p.address a " +
            "LEFT JOIN FETCH p.user u " +
            "LEFT JOIN FETCH a.province " +
            "WHERE p.user.user_id = :idUser")
    Person findByUser(@Param("idUser") Long idUser);

}