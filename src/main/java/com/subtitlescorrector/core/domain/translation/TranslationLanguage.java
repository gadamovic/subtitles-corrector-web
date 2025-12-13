package com.subtitlescorrector.core.domain.translation;

public enum TranslationLanguage {

    ARABIC("Arabic", "AR"),
    BULGARIAN("Bulgarian", "BG"),
    CHINESE_SIMPLIFIED("Chinese (Simplified)", "ZH"),
    CHINESE_TRADITIONAL("Chinese (Traditional)", "ZT"),
    CZECH("Czech", "CS"),
    DANISH("Danish", "DA"),
    DUTCH("Dutch", "NL"),
    ENGLISH("English", "EN"),
    ESTONIAN("Estonian", "ET"),
    FINNISH("Finnish", "FI"),
    FRENCH("French", "FR"),
    GERMAN("German", "DE"),
    GREEK("Greek", "EL"),
    HUNGARIAN("Hungarian", "HU"),
    INDONESIAN("Indonesian", "ID"),
    ITALIAN("Italian", "IT"),
    JAPANESE("Japanese", "JA"),
    KOREAN("Korean", "KO"),
    LATVIAN("Latvian", "LV"),
    LITHUANIAN("Lithuanian", "LT"),
    NORWEGIAN_BOKMAL("Norwegian (Bokmål)", "NB"),
    POLISH("Polish", "PL"),
    PORTUGUESE_EU("Portuguese (EU)", "PT"),
    PORTUGUESE_BR("Portuguese (Brazil)", "PT-BR"),
    ROMANIAN("Romanian", "RO"),
    RUSSIAN("Russian", "RU"),
    SLOVAK("Slovak", "SK"),
    SLOVENIAN("Slovenian", "SL"),
    SPANISH("Spanish", "ES"),
    SWEDISH("Swedish", "SV"),
    TURKISH("Turkish", "TR"),
    UKRAINIAN("Ukrainian", "UK"),
    VIETNAMESE("Vietnamese", "VI"),
    HEBREW("Hebrew", "HE"),
    THAI("Thai", "TH");

    private final String displayName;
    private final String isoCode;

    TranslationLanguage(String displayName, String isoCode) {
        this.displayName = displayName;
        this.isoCode = isoCode;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getIsoCode() {
        return isoCode;
    }
    
    public static TranslationLanguage findByDisplayName(String displayName) {
    	for(TranslationLanguage lang : values()) {
    		if(lang.getDisplayName().equals(displayName)) {
    			return lang;
    		}
    	}
    	return null;
    }
	
}
