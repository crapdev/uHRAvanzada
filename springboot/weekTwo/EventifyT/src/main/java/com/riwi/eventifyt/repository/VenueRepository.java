package com.riwi.eventifyt.repository;


import com.riwi.eventifyt.domain.Venue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VenueRepository extends JpaRepository<Venue, Long> {

    List<Venue> findByNameContainingIgnoreCase(String name);

    Page<Venue> findByNameContainingIgnoreCase(String name, Pageable pageable);


}
