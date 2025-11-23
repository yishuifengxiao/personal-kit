package com.yishuifengxiao.ecc;

import com.yishuifengxiao.common.tool.codec.X509Helper;

import java.security.cert.CertificateException;

/**
 * @author shi
 * @version 1.0.0
 * @since 1.0.0
 */
public class X509HelperTest {
    public static void main(String[] args) throws CertificateException {
        String certData = """
                -----BEGIN CERTIFICATE-----
                MIICTDCCAfGgAwIBAgIBATAKBggqhkjOPQQDAjBJMRUwEwYDVQQDDAxHU01BIFRl
                c3QgQ0kxETAPBgNVBAsMCFRFU1RDRVJUMRAwDgYDVQQKDAdSU1BURVNUMQswCQYD
                VQQGEwJJVDAeFw0xODAzMjAxMDQwMjJaFw0xOTAzMjAxMDQwMjJaMEkxFTATBgNV
                BAMMDEdTTUEgVGVzdCBDSTERMA8GA1UECwwIVEVTVENFUlQxEDAOBgNVBAoMB1JT
                UFRFU1QxCzAJBgNVBAYTAklUMFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEA/PB
                6uvmm6S81GX7+3TAgku+4mjU+YqaeONdVFFoc4PnCvtGXstiXwq3qXT/yy3JeDo9
                dp58nVQdQz1AfEVUsaOByTCBxjAdBgNVHQ4EFgQUAMOLTWNfZq1jcHrDNOWGqckM
                IgAwDwYDVR0TAQH/BAUwAwEB/zAXBgNVHSABAf8EDTALMAkGB2eBEgECAQAwDgYD
                VR0PAQH/BAQDAgEGMA4GA1UdEQQHMAWIA4g3ATBbBgNVHR8EVDBSMCegJaAjhiFo
                dHRwOi8vY2kudGVzdC5nc21hLmNvbS9DUkwtQS5jcmwwJ6AloCOGIWh0dHA6Ly9j
                aS50ZXN0LmdzbWEuY29tL0NSTC1CLmNybDAKBggqhkjOPQQDAgNJADBGAiEA49aD
                cyVvfVGH+1ezy0gT10M/oMgYStBXZjs62HG2dD0CIQCprPpRh8qwNl4toPVr159G
                GT+il8g1UqapmvFJb/jOsg==
                -----END CERTIFICATE-----
                
                """.trim();
        X509Helper.Cert fullInfo = X509Helper.extractFullInfo(certData);
        System.out.println(fullInfo);
    }
}
