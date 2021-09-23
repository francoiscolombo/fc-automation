package net.francoiscolombo.tools.automaton.cypher;

import net.francoiscolombo.tools.automaton.exceptions.CipherException;
import org.yaml.snakeyaml.Yaml;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class VaultManager {

    private final static VaultManager instance = new VaultManager();

    public static VaultManager getIntance() {
        return instance;
    }

    final private Path vaultFilePath;
    final private Map<String, Object> vault = new HashMap<>();
    final private SecretKey secretKey;
    final private String algorithm;

    private VaultManager() {
        try {
            String passwordAndSalt = generatePasswordAndSalt();
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(passwordAndSalt.toCharArray(), passwordAndSalt.getBytes(), 65536, 256);
            secretKey = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
            algorithm = "AES/ECB/PKCS5Padding";
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | CipherException e) {
            throw new RuntimeException(e.getMessage());
        }
        vaultFilePath = Paths.get(String.format("%s%s.automaton%svault.yml", System.getProperty("user.home"), File.separator, File.separator));
        if(vaultFilePath.toFile().exists() && vaultFilePath.toFile().canRead() && vaultFilePath.toFile().canWrite()) {
            Yaml yaml = new Yaml();
            try(FileInputStream vaultFile = new FileInputStream(vaultFilePath.toFile())) {
                Map<String,Object> content = yaml.load(vaultFile);
                if(content != null) {
                    vault.putAll(content);
                }
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        } else {
            try {
                Files.createDirectories(vaultFilePath.getParent());
                Files.createFile(vaultFilePath);
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    private String generatePasswordAndSalt() throws CipherException {
        try {
            StringWriter key = new StringWriter();
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface ni = networkInterfaces.nextElement();
                byte[] hardwareAddress = ni.getHardwareAddress();
                if (hardwareAddress != null) {
                    String[] hexadecimalFormat = new String[hardwareAddress.length];
                    for (int i = 0; i < hardwareAddress.length; i++) {
                        hexadecimalFormat[i] = String.format("%02X", hardwareAddress[i]);
                    }
                    key.append(String.join("", hexadecimalFormat));
                }
            }
            return key.toString();
        } catch (SocketException e) {
            CipherException cipherException = new CipherException(e.getMessage());
            cipherException.fillInStackTrace();
            throw cipherException;
        }
    }

    private String encrypt(String input) throws CipherException {
        try {
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] cipherText = cipher.doFinal(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(cipherText);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | InvalidKeyException | BadPaddingException e) {
            CipherException cipherException = new CipherException(e.getMessage());
            cipherException.fillInStackTrace();
            throw cipherException;
        }
    }

    private String decrypt(String cipherText) throws CipherException {
        try {
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText.getBytes(StandardCharsets.UTF_8)));
            return new String(plainText);
        } catch (NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
            CipherException cipherException = new CipherException(e.getMessage());
            cipherException.fillInStackTrace();
            throw cipherException;
        }
    }

    private void serializeVault() throws CipherException {
        Yaml yaml = new Yaml();
        try(FileWriter fileWriter = new FileWriter(vaultFilePath.toFile())) {
            yaml.dump(vault, fileWriter);
        } catch (IOException e) {
            CipherException cipherException = new CipherException(e.getMessage());
            cipherException.fillInStackTrace();
            throw cipherException;
        }
    }

    public void createKey(String createKey) {
        System.out.println("> Key   : "+createKey);
        try {
            String value = new String(System.console().readPassword("> Value :"));
            vault.putIfAbsent(createKey, encrypt(value));
            serializeVault();
        } catch (CipherException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void updateKey(String updateKey) {
        System.out.println("> Key   : "+updateKey);
        try {
            String value = new String(System.console().readPassword("> Value :"));
            vault.put(updateKey, encrypt(value));
            serializeVault();
        } catch (CipherException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void deleteKey(String deleteKey) {
        try {
            vault.remove(deleteKey);
            serializeVault();
        } catch (CipherException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public String retrieve(String key) throws CipherException {
        if(vault.containsKey(key)) {
            String cipherValue = String.valueOf(vault.get(key));
            return decrypt(cipherValue);
        }
        return null;
    }

    public void listKeys() {
        System.out.println("Content of the vault");
        System.out.println("--------------------");
        vault.keySet().forEach(key -> {
            System.out.printf("- %s\n", key);
        });
    }

}
