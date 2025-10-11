package com.subtitlescorrector.core.service.corrections;

import org.springframework.stereotype.Service;

import com.subtitlescorrector.core.domain.AdditionalData;

@Service
public class AdditionalDataToCorrectorParametersAdapter {

	public CorrectorParameters adapt(AdditionalData data) {
		
		CorrectorParameters params = new CorrectorParameters();
		params.setConvertAeToTj(data.getConvertAeToTj());
	
		params.setStripBTags(data.getStripBTags());
        params.setStripITags(data.getStripITags());
        params.setStripUTags(data.getStripUTags());
        params.setStripFontTags(data.getStripFontTags());

        params.setCorrectorIndex(data.getCorrectorIndex());

        params.setKeepBOM(data.getKeepBOM());

        params.setConvertAeToTj(data.getConvertAeToTj());
        params.setConvertAEToTJ(data.getConvertAEToTJ());

        params.setConvertEToch(data.getConverteToch());
        params.setConvertEToCH(data.getConvertEToCH());

        params.setNumberOfCorrectors(data.getNumberOfCorrectors());

        params.setProcessedLines(data.getProcessedLines());
        params.setTotalNumberOfLines(data.getTotalNumberOfLines());
        
		return params;
	}
	
}
