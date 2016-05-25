from M2Crypto import BIO, SMIME, X509

s = SMIME.SMIME()

s.load_key('mycert-private.pem', 'mycert.pem')

p7, data = SMIME.smime_load_pkcs7('smime.txt')

out = s.decrypt(p7)

print out