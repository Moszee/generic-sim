package com.genericsim.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "persons")
@Getter
@Setter
@NoArgsConstructor
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PersonRole role;

    @Column(nullable = false)
    private int age;

    @Column(nullable = false)
    private int health;

    @Column(nullable = false)
    private double huntingSkill = 0.5;

    @Column(nullable = false)
    private double gatheringSkill = 0.5;

    @ManyToOne
    @JoinColumn(name = "tribe_id")
    private Tribe tribe;

    @ManyToOne
    @JoinColumn(name = "family_id")
    private Family family;

    public enum PersonRole {
        HUNTER,
        GATHERER,
        CHILD,
        ELDER
    }

    public Person(String name, PersonRole role, int age, int health) {
        this.name = name;
        this.role = role;
        this.age = age;
        this.health = health;
    }

}
