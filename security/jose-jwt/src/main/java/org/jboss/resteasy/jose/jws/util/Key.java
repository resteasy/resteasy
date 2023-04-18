package org.jboss.resteasy.jose.jws.util;

import java.io.Serializable;

public class Key implements Serializable {

    public static class KeyStoreConfig implements Serializable {
        private String file;
        private String resource;
        private String password;
        private String type;
        private String alias;
        private String privateKeyAlias;
        private String privateKeyPassword;
        private String certificateAlias;

        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
        }

        public String getResource() {
            return resource;
        }

        public void setResource(String resource) {
            this.resource = resource;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getPrivateKeyAlias() {
            return privateKeyAlias;
        }

        public void setPrivateKeyAlias(String privateKeyAlias) {
            this.privateKeyAlias = privateKeyAlias;
        }

        public String getPrivateKeyPassword() {
            return privateKeyPassword;
        }

        public void setPrivateKeyPassword(String privateKeyPassword) {
            this.privateKeyPassword = privateKeyPassword;
        }

        public String getCertificateAlias() {
            return certificateAlias;
        }

        public void setCertificateAlias(String certificateAlias) {
            this.certificateAlias = certificateAlias;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }
    }

    private boolean signing;
    private boolean encryption;
    private KeyStoreConfig keystore;
    private String privateKeyPem;
    private String publicKeyPem;
    private String certificatePem;

    public boolean isSigning() {
        return signing;
    }

    public void setSigning(Boolean signing) {
        this.signing = signing != null && signing;
    }

    public boolean isEncryption() {
        return encryption;
    }

    public void setEncryption(Boolean encryption) {
        this.encryption = encryption != null && encryption;
    }

    public KeyStoreConfig getKeystore() {
        return keystore;
    }

    public void setKeystore(KeyStoreConfig keystore) {
        this.keystore = keystore;
    }

    public String getPrivateKeyPem() {
        return privateKeyPem;
    }

    public void setPrivateKeyPem(String privateKeyPem) {
        this.privateKeyPem = privateKeyPem;
    }

    public String getPublicKeyPem() {
        return publicKeyPem;
    }

    public void setPublicKeyPem(String publicKeyPem) {
        this.publicKeyPem = publicKeyPem;
    }

    public String getCertificatePem() {
        return certificatePem;
    }

    public void setCertificatePem(String certificatePem) {
        this.certificatePem = certificatePem;
    }
}
