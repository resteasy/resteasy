from M2Crypto import BIO, Rand, SMIME

def makebuf(text):
   return BIO.MemoryBuffer(text)

# Make a MemoryBuffer of the message.
buf = makebuf('a sign of our times')

# Seed the PRNG.
Rand.load_file('randpool.dat', -1)

# Instantiate an SMIME object; set it up; sign the buffer.
s = SMIME.SMIME()
s.load_key('mycert-private.pem', 'mycert.pem')
p7 = s.sign(buf, SMIME.PKCS7_DETACHED)

# Recreate buf.
buf = makebuf(
"""Content-Type: text/plain

a sign of our times""")

# Output p7 in mail-friendly format.
out = BIO.MemoryBuffer()
s.write(out, p7, buf)
#p7.write(out)
print out.read()

# Save the PRNG's state.
Rand.save_file('randpool.dat')
