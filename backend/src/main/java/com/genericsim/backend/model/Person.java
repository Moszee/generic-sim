package com.genericsim.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "persons")
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

    @ManyToOne
    @JoinColumn(name = "tribe_id")
    private Tribe tribe;

    public enum PersonRole {
        HUNTER,
        GATHERER,
        CHILD,
        ELDER
    }

    public Person() {
    }

    public Person(String name, PersonRole role, int age, int health) {
        this.name = name;
        this.role = role;
        this.age = age;
        this.health = health;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PersonRole getRole() {
        return role;
    }

    public void setRole(PersonRole role) {
        this.role = role;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public Tribe getTribe() {
        return tribe;
    }

    public void setTribe(Tribe tribe) {
        this.tribe = tribe;
    }
}
