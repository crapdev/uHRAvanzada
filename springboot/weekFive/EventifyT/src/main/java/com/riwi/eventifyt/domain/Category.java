package com.riwi.eventifyt.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Conciertos, Talleres, Conferencias, Deportes...
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(length = 255)
    private String description;
}
