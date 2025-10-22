package com.genericsim.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tribes")
@Getter
@Setter
@NoArgsConstructor
public class Tribe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @Column(nullable = false)
    private long currentTick;

    @Column(nullable = false)
    private int bondLevel = 50;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "resources_id", referencedColumnName = "id")
    private Resources resources;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "central_storage_id", referencedColumnName = "id")
    private Resources centralStorage;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "policy_id", referencedColumnName = "id")
    private Policy policy;

    @OneToMany(mappedBy = "tribe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Person> members = new ArrayList<>();

    @OneToMany(mappedBy = "tribe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Family> families = new ArrayList<>();

    public Tribe(String name, String description) {
        this.name = name;
        this.description = description;
        this.currentTick = 0;
    }

    public void addMember(Person person) {
        members.add(person);
        person.setTribe(this);
    }

    public void removeMember(Person person) {
        members.remove(person);
        person.setTribe(null);
    }

    public void addFamily(Family family) {
        families.add(family);
        family.setTribe(this);
    }

    public void removeFamily(Family family) {
        families.remove(family);
        family.setTribe(null);
    }

}
