package com.backend.bank.security.data;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Base64;

@Converter()
public class IdAttributeConverterBase64 implements AttributeConverter<Long, String> {

    @Override
    public String convertToDatabaseColumn(Long id) {
        return Base64.getEncoder().encodeToString(id.toString().getBytes());
    }

    @Override
    public Long convertToEntityAttribute(String encodedId) {
        String decodedId = new String(Base64.getDecoder().decode(encodedId));
        return Long.valueOf(decodedId);
    }
}
