package com.xxl.job.admin.core.util;

import microsoft.exchange.webservices.data.EWSConstants;
import microsoft.exchange.webservices.data.core.CookieProcessingTargetAuthenticationStrategy;
import microsoft.exchange.webservices.data.core.EwsSSLProtocolSocketFactory;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import org.apache.http.client.AuthenticationStrategy;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * @author Eliane_wu
 */
public class ExchangeServiceCustomer extends ExchangeService {

    public ExchangeServiceCustomer(ExchangeVersion requestedServerVersion) {
        super(requestedServerVersion);
    }

    protected Registry<ConnectionSocketFactory> createConnectionSocketFactoryRegistry() {
        try {
            TrustManager trustManager = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                }
                @Override
                public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                }
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            };
            return RegistryBuilder.<ConnectionSocketFactory>create()
                    .register(EWSConstants.HTTP_SCHEME, new PlainConnectionSocketFactory())
                    .register(EWSConstants.HTTPS_SCHEME, EwsSSLProtocolSocketFactory.build(trustManager))
                    .build();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(
                    "Could not initialize ConnectionSocketFactory instances for HttpClientConnectionManager", e
            );
        }
    }

    private void initializeHttpClient() {
        Registry<ConnectionSocketFactory> registry = this.createConnectionSocketFactoryRegistry();
        HttpClientConnectionManager httpConnectionManager = new BasicHttpClientConnectionManager(registry);
        AuthenticationStrategy authStrategy = new CookieProcessingTargetAuthenticationStrategy();
        this.httpClient = HttpClients.custom().setConnectionManager(httpConnectionManager).setTargetAuthenticationStrategy(authStrategy).build();
    }

}
