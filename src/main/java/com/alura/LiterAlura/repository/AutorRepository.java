package com.alura.LiterAlura.repository;

import com.alura.LiterAlura.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AutorRepository extends JpaRepository<Autor, Long> {
    Optional<Autor> findByName(String name);

    @Query("SELECT a FROM Autor a WHERE a.birthYear > :dataInicial AND a.deathYear < :dataFinal")
    List<Autor> findByDate(@Param("dataInicial") int dataInicial, @Param("dataFinal") int dataFinal);

}
