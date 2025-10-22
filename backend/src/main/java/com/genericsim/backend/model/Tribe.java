package com.genericsim.backend.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tribes")
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

    public Tribe() {
    }

    public Tribe(String name, String description) {
        this.name = name;
        this.description = description;
        this.currentTick = 0;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getCurrentTick() {
        return currentTick;
    }

    public void setCurrentTick(long currentTick) {
        this.currentTick = currentTick;
    }

    public Resources getResources() {
        return resources;
    }

    public void setResources(Resources resources) {
        this.resources = resources;
    }

    public Policy getPolicy() {
        return policy;
    }

    public void setPolicy(Policy policy) {
        this.policy = policy;
    }

    public List<Person> getMembers() {
        return members;
    }

    public void setMembers(List<Person> members) {
        this.members = members;
    }

    public void addMember(Person person) {
        members.add(person);
        person.setTribe(this);
    }

    public void removeMember(Person person) {
        members.remove(person);
        person.setTribe(null);
    }

    public List<Family> getFamilies() {
        return families;
    }

    public void setFamilies(List<Family> families) {
        this.families = families;
    }

    public void addFamily(Family family) {
        families.add(family);
        family.setTribe(this);
    }

    public void removeFamily(Family family) {
        families.remove(family);
        family.setTribe(null);
    }

    public int getBondLevel() {
        return bondLevel;
    }

    public void setBondLevel(int bondLevel) {
        this.bondLevel = bondLevel;
    }

    public Resources getCentralStorage() {
        return centralStorage;
    }

    public void setCentralStorage(Resources centralStorage) {
        this.centralStorage = centralStorage;
    }
}
