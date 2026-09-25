package com.riwi.eventifyt.repository;

import com.riwi.eventifyt.domain.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface EventRepository extends JpaRepository<Event, Long> {

    // Consulta derivada: Spring genera el SQL a partir del nombre del metodo
    List<Event> findByNameContainingIgnoreCase(String name);

    // Misma consulta, pero paginada (la usaremos en el Bloque 7)
    Page<Event> findByNameContainingIgnoreCase(String name, Pageable pageable);


}
