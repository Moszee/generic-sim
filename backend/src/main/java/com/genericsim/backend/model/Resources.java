package com.genericsim.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "resources")
@Getter
@Setter
@NoArgsConstructor
public class Resources {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int food;

    @Column(nullable = false)
    private int water;

    @OneToOne(mappedBy = "resources")
    private Tribe tribe;

    public Resources(int food, int water) {
        this.food = food;
        this.water = water;
    }

}
