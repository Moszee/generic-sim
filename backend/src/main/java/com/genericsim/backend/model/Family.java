package com.genericsim.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "families")
@Getter
@Setter
@NoArgsConstructor
public class Family {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "tribe_id")
    private Tribe tribe;

    @OneToMany(mappedBy = "family", cascade = CascadeType.ALL)
    private List<Person> members = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "storage_id", referencedColumnName = "id")
    private Resources storage;

    public Family(String name) {
        this.name = name;
        this.storage = new Resources(0, 0);
    }

    public void addMember(Person person) {
        members.add(person);
        person.setFamily(this);
    }

    public void removeMember(Person person) {
        members.remove(person);
        person.setFamily(null);
    }

}
