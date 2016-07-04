from M2Crypto import SMIME, X509

# Instantiate an SMIME object.
s = SMIME.SMIME()

# Load the signer's cert.
x509 = X509.load_cert('bad.pem')
sk = X509.X509_Stack()
sk.push(x509)
s.set_x509_stack(sk)

# Load the signer's CA cert. In this case, because the signer's
# cert is self-signed, it is the signer's cert itself.
st = X509.X509_Store()
st.load_info('bad.pem')
s.set_x509_store(st)

# Load the data, verify it.
p7, data = SMIME.smime_load_pkcs7('target/smime_signed.txt')
v = s.verify(p7, data)
print v
print data
print data.read()
