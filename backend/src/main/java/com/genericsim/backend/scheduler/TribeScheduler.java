package com.genericsim.backend.scheduler;

import com.genericsim.backend.model.Tribe;
import com.genericsim.backend.repository.TribeRepository;
import com.genericsim.backend.service.TribeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TribeScheduler {

    private static final Logger logger = LoggerFactory.getLogger(TribeScheduler.class);
    
    private final TribeService tribeService;
    private final TribeRepository tribeRepository;

    public TribeScheduler(TribeService tribeService, TribeRepository tribeRepository) {
        this.tribeService = tribeService;
        this.tribeRepository = tribeRepository;
    }

    // Runs every day at midnight (cron: second, minute, hour, day, month, weekday)
    @Scheduled(cron = "0 0 0 * * *")
    public void processDailyTick() {
        logger.info("Processing daily tick for all tribes");
        
        List<Tribe> tribes = tribeRepository.findAll();
        for (Tribe tribe : tribes) {
            try {
                tribeService.processTick(tribe.getId());
                logger.info("Processed tick for tribe: {} (ID: {})", tribe.getName(), tribe.getId());
            } catch (Exception e) {
                logger.error("Error processing tick for tribe {}: {}", tribe.getId(), e.getMessage());
            }
        }
        
        logger.info("Daily tick processing completed. Processed {} tribes", tribes.size());
    }
}
