package com.billing.entity;

/**
 * Enumeration of Spanish provinces with their corresponding postal code prefixes
 */
public enum SpanishProvince {
    ALAVA("01", "Álava"),
    ALBACETE("02", "Albacete"),
    ALICANTE("03", "Alicante"),
    ALMERIA("04", "Almería"),
    AVILA("05", "Ávila"),
    BADAJOZ("06", "Badajoz"),
    BALEARES("07", "Baleares"),
    BARCELONA("08", "Barcelona"),
    BURGOS("09", "Burgos"),
    CACERES("10", "Cáceres"),
    CADIZ("11", "Cádiz"),
    CASTELLON("12", "Castellón"),
    CIUDAD_REAL("13", "Ciudad Real"),
    CORDOBA("14", "Córdoba"),
    CORUNA("15", "A Coruña"),
    CUENCA("16", "Cuenca"),
    GIRONA("17", "Girona"),
    GRANADA("18", "Granada"),
    GUADALAJARA("19", "Guadalajara"),
    GUIPUZCOA("20", "Gipuzkoa"),
    HUELVA("21", "Huelva"),
    HUESCA("22", "Huesca"),
    JAEN("23", "Jaén"),
    LEON("24", "León"),
    LLEIDA("25", "Lleida"),
    RIOJA("26", "La Rioja"),
    MADRID("28", "Madrid"),
    MALAGA("29", "Málaga"),
    MURCIA("30", "Murcia"),
    NAVARRA("31", "Navarra"),
    OURENSE("32", "Ourense"),
    ASTURIAS("33", "Asturias"),
    PALENCIA("34", "Palencia"),
    PALMAS("35", "Las Palmas"),
    PONTEVEDRA("36", "Pontevedra"),
    SALAMANCA("37", "Salamanca"),
    SANTA_CRUZ("38", "Santa Cruz de Tenerife"),
    CANTABRIA("39", "Cantabria"),
    SEGOVIA("40", "Segovia"),
    SEVILLA("41", "Sevilla"),
    SORIA("42", "Soria"),
    TARRAGONA("43", "Tarragona"),
    TERUEL("44", "Teruel"),
    TOLEDO("45", "Toledo"),
    VALENCIA("46", "Valencia"),
    VALLADOLID("47", "Valladolid"),
    VIZCAYA("48", "Bizkaia"),
    ZAMORA("49", "Zamora"),
    ZARAGOZA("50", "Zaragoza"),
    CEUTA("51", "Ceuta"),
    MELILLA("52", "Melilla");
    
    private final String code;
    private final String displayName;
    
    SpanishProvince(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Validates if a postal code matches the province
     * @param postalCode 5-digit postal code
     * @return true if the postal code belongs to this province
     */
    public boolean isValidPostalCode(String postalCode) {
        if (postalCode == null || postalCode.length() != 5) {
            return false;
        }
        return postalCode.startsWith(this.code);
    }
    
    /**
     * Gets province by postal code prefix
     * @param postalCode 5-digit postal code
     * @return SpanishProvince that matches the postal code, or null if not found
     */
    public static SpanishProvince getByPostalCode(String postalCode) {
        if (postalCode == null || postalCode.length() != 5) {
            return null;
        }
        
        String prefix = postalCode.substring(0, 2);
        for (SpanishProvince province : values()) {
            if (province.code.equals(prefix)) {
                return province;
            }
        }
        return null;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}