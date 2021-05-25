package com.telcaria.dashboards_manager.nbi.wrapper;

import lombok.Data;
import lombok.NonNull;

@Data
public class LogScrapperRequestWrapper {
    private String nsid;
    private String vnfid;
    private String performanceMetric;
    private String kafkaTopic;
    private String interval;
    private String expression;
}