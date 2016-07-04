    from M2Crypto import EVP, RSA, X509
import binascii

key = EVP.load_key("mycert-private.pem")
key.reset_context(md='sha256')
key.sign_init()
key.sign_update("from-python")
signature = key.sign_final()
print "Signature:"
print binascii.b2a_hex(signature)

print "Verifying"
hexSignature = "0a11ab4ebcd2b0803d6e280a1d45b5b5d5d53688949f5a4f2d6436f15df3b10633c79760b9fe3b64eb9d84371c35e8b7d946052dfdd99ebb5cf7f3092762e1a91b261117e6675f2d28afe2ec4d90abfe3559a1259d2c66f3dc42ca3bfce7498705833445170bd8c293d60448b6c599abfe2d06882d3fff9ef887379eb7da3fe0"
java_sig = binascii.a2b_hex(hexSignature)

cert = X509.load_cert("mycert.pem")
pubkey = cert.get_pubkey()
pubkey.reset_context(md="sha256")
pubkey.verify_init()
pubkey.verify_update("from-java")
assert pubkey.verify_final(java_sig) == 1







