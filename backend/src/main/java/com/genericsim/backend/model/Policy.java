package com.genericsim.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "policies")
@Getter
@Setter
@NoArgsConstructor
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

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SharingPriority sharingPriority = SharingPriority.ELDER;

    @Column(nullable = false)
    private boolean enableCentralStorage = false;

    @Column(nullable = false)
    private int centralStorageTaxRate = 10;

    @Column(nullable = false)
    private double storageDecayRate = 0.1;

    @Column(nullable = false)
    private int storageDecayInterval = 20;

    @OneToOne(mappedBy = "policy")
    private Tribe tribe;

    public enum SharingPriority {
        ELDER,
        CHILD,
        HUNTER,
        GATHERER,
        YOUNGEST,
        RANDOM
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

}
