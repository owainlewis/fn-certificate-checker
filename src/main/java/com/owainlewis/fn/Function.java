package com.owainlewis.fn;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.URL;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Optional;

public class Function {
    /**
     * Given an [[X509Certificate]], get the number of days left until the certificate expires
     *
     * @param cert An [[X509Certificate]]
     *
     * @return The number of days left until the certificate expires
     */
    public long getDaysUntilExpiryFromCertificate(X509Certificate cert) {
        Date expiresOn = cert.getNotAfter();
        Date now = new Date();
        long divisor = (1000 * 60 * 60 * 24);
        return (expiresOn.getTime() - now.getTime()) / divisor;
    }

    /**
     * Fetch a certificate from a URL, making sure that the right cert is selected based on the CN
     *
     * @param url String the url to fetch a certificate from
     * @param cn String the CN of the certificate you want to check against
     *
     * @return Optional<Long> the number of days left until expiry
     * @throws IOException
     */
    public Optional<Long> getCertificateDaysUntilExpiry(URL url, String cn) throws IOException {
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.connect();
        Certificate[] certs = conn.getServerCertificates();
        for (Certificate c : certs) {
            X509Certificate x509 = (X509Certificate) c;
            String dn = x509.getSubjectDN().getName();
            if (dn.contains(cn)) {
                return Optional.of(getDaysUntilExpiryFromCertificate(x509));
            }
        }

        return Optional.empty();
    }

    public String handleRequest(CertificateCheckInput input) {
        System.out.println("Checking certificate age for " + input.getUrl());

        try {
            URL url = new URL(input.getUrl());
            Optional<Long> remainingDays = getCertificateDaysUntilExpiry(url, input.getCn());
            if (remainingDays.isPresent()) {
                return String.format("Days remaining: %d", remainingDays.get());
            }
        } catch (IOException e) {
            System.out.println("Failed to get certificate " + e);
        }

        return "Could not get certificate";
    }

    @AllArgsConstructor
    @NoArgsConstructor
    public static class CertificateCheckInput {
        @Getter
        @Setter
        String url;
        @Getter
        @Setter
        String cn;
    }
}
