package com.subtitlescorrector.core.domain;

import java.time.Instant;

public class SubtitleCorrectionEvent {

    private String fileId;
    private Instant eventTimestamp;
    private String detectedEncoding;
    private String correction;
    private String processedPercentage;

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public Instant getEventTimestamp() {
        return eventTimestamp;
    }

    public void setEventTimestamp(Instant eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
    }

    public String getDetectedEncoding() {
        return detectedEncoding;
    }

    public void setDetectedEncoding(String detectedEncoding) {
        this.detectedEncoding = detectedEncoding;
    }

    public String getCorrection() {
        return correction;
    }

    public void setCorrection(String correction) {
        this.correction = correction;
    }

    public String getProcessedPercentage() {
        return processedPercentage;
    }

    public void setProcessedPercentage(String processedPercentage) {
        this.processedPercentage = processedPercentage;
    }
}

