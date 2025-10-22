package com.genericsim.backend.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "families")
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

    public Family() {
    }

    public Family(String name) {
        this.name = name;
        this.storage = new Resources(0, 0);
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

    public Tribe getTribe() {
        return tribe;
    }

    public void setTribe(Tribe tribe) {
        this.tribe = tribe;
    }

    public List<Person> getMembers() {
        return members;
    }

    public void setMembers(List<Person> members) {
        this.members = members;
    }

    public void addMember(Person person) {
        members.add(person);
        person.setFamily(this);
    }

    public void removeMember(Person person) {
        members.remove(person);
        person.setFamily(null);
    }

    public Resources getStorage() {
        return storage;
    }

    public void setStorage(Resources storage) {
        this.storage = storage;
    }
}
