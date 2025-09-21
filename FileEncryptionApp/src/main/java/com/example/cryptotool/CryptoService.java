package com.example.cryptotool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

public class CryptoService {
    private static final Logger logger = LoggerFactory.getLogger(CryptoService.class);
    private static final String KEY_DERIVATION_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final String TRANSFORMATION_STRING = "AES/GCM/NoPadding";
    private static final int SALT_LENGTH_BYTES = 16;
    private static final int IV_LENGTH_BYTES = 12;
    private static final int KEY_LENGTH_BITS = 256;
    private static final int ITERATION_COUNT = 65536;
    private static final int GCM_TAG_LENGTH_BITS = 128;

    public void encrypt(char[] password, File keyFile, File inputFile, File outputFile, AtomicBoolean cancellationFlag, BiConsumer<Long, Long> progressUpdater) throws CryptoException {
        process(Cipher.ENCRYPT_MODE, password, keyFile, inputFile, outputFile, cancellationFlag, progressUpdater);
    }

    public void decrypt(char[] password, File keyFile, File inputFile, File outputFile, AtomicBoolean cancellationFlag, BiConsumer<Long, Long> progressUpdater) throws CryptoException {
        process(Cipher.DECRYPT_MODE, password, keyFile, inputFile, outputFile, cancellationFlag, progressUpdater);
    }

    private void process(int mode, char[] password, File keyFile, File inputFile, File outputFile, AtomicBoolean cancellationFlag, BiConsumer<Long, Long> progressUpdater) throws CryptoException {
        try (FileInputStream fis = new FileInputStream(inputFile);
             FileOutputStream fos = new FileOutputStream(outputFile)) {

            byte[] salt;
            byte[] iv;
            SecretKey secretKey;

            if (mode == Cipher.ENCRYPT_MODE) {
                salt = generateRandomBytes(SALT_LENGTH_BYTES);
                iv = generateRandomBytes(IV_LENGTH_BYTES);
                fos.write(salt);
                fos.write(iv);
                secretKey = deriveKey(password, salt, keyFile);
            } else {
                salt = new byte[SALT_LENGTH_BYTES];
                iv = new byte[IV_LENGTH_BYTES];
                if (fis.read(salt) != SALT_LENGTH_BYTES || fis.read(iv) != IV_LENGTH_BYTES) {
                    throw new CryptoException("Invalid file format: could not read salt/IV.");
                }
                secretKey = deriveKey(password, salt, keyFile);
            }

            Cipher cipher = Cipher.getInstance(TRANSFORMATION_STRING);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv);
            cipher.init(mode, secretKey, parameterSpec);

            long totalBytes = inputFile.length();
            long bytesProcessed = (mode == Cipher.DECRYPT_MODE) ? (long)SALT_LENGTH_BYTES + IV_LENGTH_BYTES : 0;
            byte[] buffer = new byte[8192];
            int bytesRead;

            try (CipherInputStream cis = new CipherInputStream(fis, cipher)) {
                while ((bytesRead = cis.read(buffer)) != -1) {
                    if (cancellationFlag.get()) {
                        logger.warn("Operation cancelled by user.");
                        return;
                    }
                    fos.write(buffer, 0, bytesRead);
                    bytesProcessed += bytesRead;
                    progressUpdater.accept(bytesProcessed, totalBytes);
                }
            }
        } catch (Exception e) {
            logger.error("Cryptographic operation failed.", e);
            throw new CryptoException("Operation failed: " + e.getMessage(), e);
        }
    }

    public void secureDelete(File file) throws CryptoException {
        try {
            if (file.exists()) {
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    byte[] randomData = new byte[(int) file.length()];
                    new SecureRandom().nextBytes(randomData);
                    fos.write(randomData);
                }
                Files.delete(file.toPath());
                logger.info("Securely deleted file: {}", file.getAbsolutePath());
            }
        } catch (IOException e) {
            logger.error("Failed to securely delete file: {}", file.getAbsolutePath(), e);
            throw new CryptoException("Could not securely delete original file.", e);
        }
    }

    private SecretKey deriveKey(char[] password, byte[] salt, File keyFile) throws GeneralSecurityException, IOException {
        byte[] passwordBytes = new String(password).getBytes();
        byte[] keyFileBytes = new byte[0];
        if (keyFile != null && keyFile.exists()) {
            keyFileBytes = Files.readAllBytes(keyFile.toPath());
        }

        byte[] combinedSource = new byte[passwordBytes.length + keyFileBytes.length];
        System.arraycopy(passwordBytes, 0, combinedSource, 0, passwordBytes.length);
        System.arraycopy(keyFileBytes, 0, combinedSource, passwordBytes.length, keyFileBytes.length);

        SecretKeyFactory factory = SecretKeyFactory.getInstance(KEY_DERIVATION_ALGORITHM);
        KeySpec spec = new PBEKeySpec(new String(combinedSource).toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH_BITS);
        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), "AES");
    }

    private byte[] generateRandomBytes(int length) {
        byte[] bytes = new byte[length];
        new SecureRandom().nextBytes(bytes);
        return bytes;
    }
}

