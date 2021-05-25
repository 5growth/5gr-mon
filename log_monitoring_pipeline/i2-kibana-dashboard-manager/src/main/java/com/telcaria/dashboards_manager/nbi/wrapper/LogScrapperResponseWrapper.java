package com.telcaria.dashboards_manager.nbi.wrapper;

import lombok.Data;
import lombok.NonNull;

@Data
public class LogScrapperResponseWrapper {
    private String scraper_id;
    private String nsid;
    private String vnfid;
    private String performanceMetric;
    private String kafkaTopic;
    private String interval;
    private String expression;
}