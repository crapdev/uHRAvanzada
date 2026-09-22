package com.riwi.eventifyt.domain;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    @Id  // clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // la BD asigna el id (1, 2, 3...)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private LocalDate date;

    @Column(length = 500)
    private String description;







}
