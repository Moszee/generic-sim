package com.genericsim.backend.dto;

/**
 * Data Transfer Object for updating tribe policy.
 * Contains the policy parameters that can be modified.
 */
public class PolicyUpdateDTO {
    private Integer foodTaxRate;
    private Integer waterTaxRate;
    private Integer huntingIncentive;
    private Integer gatheringIncentive;
    
    public PolicyUpdateDTO() {
    }
    
    public PolicyUpdateDTO(Integer foodTaxRate, Integer waterTaxRate, 
                           Integer huntingIncentive, Integer gatheringIncentive) {
        this.foodTaxRate = foodTaxRate;
        this.waterTaxRate = waterTaxRate;
        this.huntingIncentive = huntingIncentive;
        this.gatheringIncentive = gatheringIncentive;
    }
    
    public Integer getFoodTaxRate() {
        return foodTaxRate;
    }
    
    public void setFoodTaxRate(Integer foodTaxRate) {
        this.foodTaxRate = foodTaxRate;
    }
    
    public Integer getWaterTaxRate() {
        return waterTaxRate;
    }
    
    public void setWaterTaxRate(Integer waterTaxRate) {
        this.waterTaxRate = waterTaxRate;
    }
    
    public Integer getHuntingIncentive() {
        return huntingIncentive;
    }
    
    public void setHuntingIncentive(Integer huntingIncentive) {
        this.huntingIncentive = huntingIncentive;
    }
    
    public Integer getGatheringIncentive() {
        return gatheringIncentive;
    }
    
    public void setGatheringIncentive(Integer gatheringIncentive) {
        this.gatheringIncentive = gatheringIncentive;
    }
}
