package com.riwi.eventifyt.repository;


import com.riwi.eventifyt.domain.Venue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VenueRepository extends JpaRepository<Venue, Long> {

    List<Venue> findByNameContainingIgnoreCase(String name);

    Page<Venue> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // ¿Ya existe un lugar con ese nombre? (para no crear duplicados)
    boolean existsByNameIgnoreCase(String name);

    // Igual, pero sin contar al propio lugar (para actualizar sin "chocar" consigo mismo)
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

}
