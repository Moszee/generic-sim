package com.genericsim.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "policies")
public class Policy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @Column(nullable = false)
    private int foodTaxRate;

    @Column(nullable = false)
    private int waterTaxRate;

    @Column(nullable = false)
    private int huntingIncentive;

    @Column(nullable = false)
    private int gatheringIncentive;

    @OneToOne(mappedBy = "policy")
    private Tribe tribe;

    public Policy() {
    }

    public Policy(String name, String description, int foodTaxRate, int waterTaxRate, 
                  int huntingIncentive, int gatheringIncentive) {
        this.name = name;
        this.description = description;
        this.foodTaxRate = foodTaxRate;
        this.waterTaxRate = waterTaxRate;
        this.huntingIncentive = huntingIncentive;
        this.gatheringIncentive = gatheringIncentive;
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

    public int getFoodTaxRate() {
        return foodTaxRate;
    }

    public void setFoodTaxRate(int foodTaxRate) {
        this.foodTaxRate = foodTaxRate;
    }

    public int getWaterTaxRate() {
        return waterTaxRate;
    }

    public void setWaterTaxRate(int waterTaxRate) {
        this.waterTaxRate = waterTaxRate;
    }

    public int getHuntingIncentive() {
        return huntingIncentive;
    }

    public void setHuntingIncentive(int huntingIncentive) {
        this.huntingIncentive = huntingIncentive;
    }

    public int getGatheringIncentive() {
        return gatheringIncentive;
    }

    public void setGatheringIncentive(int gatheringIncentive) {
        this.gatheringIncentive = gatheringIncentive;
    }

    public Tribe getTribe() {
        return tribe;
    }

    public void setTribe(Tribe tribe) {
        this.tribe = tribe;
    }
}
