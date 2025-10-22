package com.genericsim.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "resources")
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

    public Resources() {
    }

    public Resources(int food, int water) {
        this.food = food;
        this.water = water;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getFood() {
        return food;
    }

    public void setFood(int food) {
        this.food = food;
    }

    public int getWater() {
        return water;
    }

    public void setWater(int water) {
        this.water = water;
    }

    public Tribe getTribe() {
        return tribe;
    }

    public void setTribe(Tribe tribe) {
        this.tribe = tribe;
    }
}
