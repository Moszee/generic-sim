package com.genericsim.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object for updating tribe policy.
 * Contains the policy parameters that can be modified.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PolicyUpdateDTO {
    private Integer foodTaxRate;
    private Integer waterTaxRate;
    private Integer huntingIncentive;
    private Integer gatheringIncentive;
    private String sharingPriority;
    private Boolean enableCentralStorage;
    private Integer centralStorageTaxRate;
    private Double storageDecayRate;
    private Integer storageDecayInterval;
    
    public PolicyUpdateDTO(Integer foodTaxRate, Integer waterTaxRate, 
                           Integer huntingIncentive, Integer gatheringIncentive) {
        this.foodTaxRate = foodTaxRate;
        this.waterTaxRate = waterTaxRate;
        this.huntingIncentive = huntingIncentive;
        this.gatheringIncentive = gatheringIncentive;
    }
}
