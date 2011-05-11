server.com. IN    SOA   dns.server.com.  unlogic.server.com.  (
                                        20091118     ; Serial
                                        5M     ; Refresh
                                        2M    ; Retry
                                        1D     ; Expiry
                                        5M )   ; Minimum
@                       IN      NS      dns.server.com.	
anil._domainKey        IN      TXT     "v=DKIM1; p=MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQChcHmXyBAbypCU3UxjKhBdFPkivQYr75Fon1FONugs7KcLtBHrl+Y6P8rZ5Yn80q/bvfkynswJlidMudFj7PQ/kenRSIansNcbD9vPvNHu+CzumsRrR3t0HjKrR0mmW+27UVesTPeZ+EDPChNOL9RYXcJoRBJOsC5pVAzSt21cbwIDAQAB;t=s"