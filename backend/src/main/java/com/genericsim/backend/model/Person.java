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

    public enum AgeGroup {
        CHILD(0, 15),
        YOUNG_ADULT(16, 40),
        ADULT(41, 59),
        ELDER(60, Integer.MAX_VALUE);

        private final int minAge;
        private final int maxAge;

        AgeGroup(int minAge, int maxAge) {
            this.minAge = minAge;
            this.maxAge = maxAge;
        }

        public int getMinAge() {
            return minAge;
        }

        public int getMaxAge() {
            return maxAge;
        }

        public static AgeGroup fromAge(int age) {
            for (AgeGroup group : values()) {
                if (age >= group.minAge && age <= group.maxAge) {
                    return group;
                }
            }
            return CHILD; // Default fallback
        }
    }

    public Person(String name, PersonRole role, int age, int health) {
        this.name = name;
        this.role = role;
        this.age = age;
        this.health = health;
    }

    public AgeGroup getAgeGroup() {
        return AgeGroup.fromAge(this.age);
    }

}
