package com.forex.autotest.utility;

import org.apache.mina.filter.ssl.KeyStoreFactory;
import org.apache.mina.filter.ssl.SslContextFactory;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.security.KeyStore;

public class SSLContextGenerator
{
    private String keyStorePath;
    private String trustStorePath;

    public SSLContextGenerator(String keyStorePath, String trustStorePath){
        this.keyStorePath=keyStorePath;
        this.trustStorePath=trustStorePath;
    }

    public SSLContext getSslContext()
    {
        SSLContext sslContext = null;
        try
        {
            File keyStoreFile = new File(keyStorePath);
            File trustStoreFile = new File(trustStorePath);

            if (keyStoreFile.exists() && trustStoreFile.exists()) {
                final KeyStoreFactory keyStoreFactory = new KeyStoreFactory();
                System.out.println("Url is: " + keyStoreFile.getAbsolutePath());
                keyStoreFactory.setDataFile(keyStoreFile);
                keyStoreFactory.setPassword("TESTTEST1");

                final KeyStoreFactory trustStoreFactory = new KeyStoreFactory();
                trustStoreFactory.setDataFile(trustStoreFile);
                trustStoreFactory.setPassword("TESTTEST1");

                final SslContextFactory sslContextFactory = new SslContextFactory();
                final KeyStore keyStore = keyStoreFactory.newInstance();
                sslContextFactory.setKeyManagerFactoryKeyStore(keyStore);

                final KeyStore trustStore = trustStoreFactory.newInstance();
                sslContextFactory.setTrustManagerFactoryKeyStore(trustStore);
                sslContextFactory.setKeyManagerFactoryKeyStorePassword("TESTTEST1");
                sslContext = sslContextFactory.newInstance();
                System.out.println("SSL provider is: " + sslContext.getProvider());
            } else {
                System.out.println("Keystore or Truststore file does not exist");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return sslContext;
    }
}