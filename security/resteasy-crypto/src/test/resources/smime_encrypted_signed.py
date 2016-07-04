from M2Crypto import BIO, SMIME, X509

s = SMIME.SMIME()

# Load private key and cert.
s.load_key('mycert-private.pem', 'mycert.pem')

# Load the signed/encrypted data.
p7, data = SMIME.smime_load_pkcs7('target/python_encrypted_signed.txt')

# After the above step, 'data' == None.
# Decrypt p7. 'out' now contains a PKCS #7 signed blob.
out = s.decrypt(p7)

# Load the signer's cert.
x509 = X509.load_cert('mycert.pem')
sk = X509.X509_Stack()
sk.push(x509)
s.set_x509_stack(sk)

# Load the signer's CA cert. In this case, because the signer's
# cert is self-signed, it is the signer's cert itself.
st = X509.X509_Store()
st.load_info('mycert.pem')
s.set_x509_store(st)

# Recall 'out' contains a PKCS #7 blob.
# Transform 'out'; verify the resulting PKCS #7 blob.
p7_bio = BIO.MemoryBuffer(out)
p7, data = SMIME.smime_load_pkcs7_bio(p7_bio)
v = s.verify(p7, data)

print v