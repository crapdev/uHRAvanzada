package com.riwi.Eventify.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    private Long id;
    private String name;
    private LocalDate date;
    private String description;







}
