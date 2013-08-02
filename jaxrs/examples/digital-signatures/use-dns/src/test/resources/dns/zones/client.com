client.com. IN    SOA   dns.client.com.  unlogic.client.com.  (
                                        20091118     ; Serial
                                        5M     ; Refresh
                                        2M    ; Retry
                                        1D     ; Expiry
                                        5M )   ; Minimum
@                       IN      NS      dns.client.com.	
bill._domainKey        IN      TXT     "v=DKIM1; p=MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCzMqdNr6bWxQdHUpuEEyb6UYyaflLNnCMois2v7JXTh33wn6+6lb5MDOpwPzg9v1Zj76YYuwybqqw7l2F3HBy2BsUEdT1WXap4UjMW1AJ5NHkHn82MXMcmyhbtJvne15INT+KmTEfu7tBgeL7U8cpKn/fBIAvC596Lm7soZw5xvwIDAQAB; t=s"