package com.backend.bank.security.data;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Value;

import java.security.spec.KeySpec;
import java.util.Base64;

@Log4j2
@Converter()
public class EncryptionAttributeConverter implements AttributeConverter<String, String> {

    @Value("${database.secret-key-password}")
    private String SECRET_KEY_PASSWORD;

    @Value("${database.salt}")
    private String SALT;

    @Value("${database.iterations}")
    private int ITERATIONS;

    @Value("${database.algorithm}")
    private String ALGORITHM;

    @Value("${database.secret-factory.algorithm}")
    private String SECRET_KEY_FACTORY_ALGORITHM;

    private SecretKeySpec getKeySpec() {
        KeySpec spec = new PBEKeySpec(SECRET_KEY_PASSWORD.toCharArray(), SALT.getBytes(), ITERATIONS, 256);
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance(SECRET_KEY_FACTORY_ALGORITHM);
            return new SecretKeySpec(skf.generateSecret(spec).getEncoded(), ALGORITHM);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("Key generation error", e);
        }
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec keySpec = getKeySpec();
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);

            String encryptedData =  Base64.getUrlEncoder().encodeToString(cipher.doFinal(attribute.getBytes()));

            log.info("encrypted data: {}", encryptedData);

            return encryptedData;
        } catch (Exception e) {
            log.error("Encryption failed for data [{}]: {} with error: {}", attribute, e, e.getMessage(), e.getCause());
            throw new RuntimeException("Encryption error", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec keySpec = getKeySpec();
            cipher.init(Cipher.DECRYPT_MODE, keySpec);

            String decryptedData = new String(cipher.doFinal(Base64.getUrlDecoder().decode(dbData)));

            log.info("Decrypted data: {}", decryptedData);

            return decryptedData;
        } catch (Exception e) {
            log.error("Decryption failed for data [{}]: {} with error: {}", dbData, e, e.getMessage(), e.getCause());
            throw new RuntimeException("Decryption error", e);
        }
    }
}
