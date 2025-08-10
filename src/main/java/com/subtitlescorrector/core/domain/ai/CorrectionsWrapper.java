package com.subtitlescorrector.core.domain.ai;

import java.util.List;

public class CorrectionsWrapper {
    private List<CorrectionResponse> corrections;

    public List<CorrectionResponse> getCorrections() {
        return corrections;
    }

    public void setCorrections(List<CorrectionResponse> corrections) {
        this.corrections = corrections;
    }
}
