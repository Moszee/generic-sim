package com.genericsim.backend.dto;

import com.genericsim.backend.model.Policy;

/**
 * Data Transfer Object for updating tribe policy.
 * Contains the policy parameters that can be modified.
 */
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

    public String getSharingPriority() {
        return sharingPriority;
    }

    public void setSharingPriority(String sharingPriority) {
        this.sharingPriority = sharingPriority;
    }

    public Boolean getEnableCentralStorage() {
        return enableCentralStorage;
    }

    public void setEnableCentralStorage(Boolean enableCentralStorage) {
        this.enableCentralStorage = enableCentralStorage;
    }

    public Integer getCentralStorageTaxRate() {
        return centralStorageTaxRate;
    }

    public void setCentralStorageTaxRate(Integer centralStorageTaxRate) {
        this.centralStorageTaxRate = centralStorageTaxRate;
    }

    public Double getStorageDecayRate() {
        return storageDecayRate;
    }

    public void setStorageDecayRate(Double storageDecayRate) {
        this.storageDecayRate = storageDecayRate;
    }

    public Integer getStorageDecayInterval() {
        return storageDecayInterval;
    }

    public void setStorageDecayInterval(Integer storageDecayInterval) {
        this.storageDecayInterval = storageDecayInterval;
    }
}
