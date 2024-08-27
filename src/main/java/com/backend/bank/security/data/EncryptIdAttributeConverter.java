package com.backend.bank.security.data;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import java.security.spec.KeySpec;
import java.util.Base64;

@Converter(autoApply = false)
public class EncryptIdAttributeConverter implements AttributeConverter<Long, String> {

    @Value("${database.secret-key-password}")
    private String SECRET_KEY_PASSWORD = "yourSecretKey";

    @Value("${database.salt}")
    private String SALT;

    @Value("${database.iterations}")
    private int ITERATIONS;

    @Value("${database.algorithm}")
    private String ALGORITHM;

    @Value("${database.secret-factory.algorithm}")
    private String SECRET_KEY_FACTORY_ALGORITHM;

    private SecretKeySpec getKeySpec() {
        try {
            KeySpec spec = new PBEKeySpec(SECRET_KEY_PASSWORD.toCharArray(), SALT.getBytes(), ITERATIONS, 256);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(SECRET_KEY_FACTORY_ALGORITHM);
            return new SecretKeySpec(skf.generateSecret(spec).getEncoded(), ALGORITHM);
        } catch (Exception e) {
            throw new RuntimeException("Key generation error", e);
        }
    }

    @Override
    public String convertToDatabaseColumn(Long id) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec keySpec = getKeySpec();
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            return Base64.getEncoder().encodeToString(cipher.doFinal(id.toString().getBytes()));
        } catch (Exception e) {
            throw new RuntimeException("Encryption error", e);
        }
    }

    @Override
    public Long convertToEntityAttribute(String encodedId) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec keySpec = getKeySpec();
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            String decryptedId = new String(cipher.doFinal(Base64.getDecoder().decode(encodedId)));
            return Long.valueOf(decryptedId);
        } catch (Exception e) {
            throw new RuntimeException("Decryption error", e);
        }
    }
}
