# Test all single-word arithmetic instructions
# Run this test with the core in debug mode and step instruction by instruction
# looking for the expected value in the return register

# Prepare some interesting values
movw %a1l $0xFF00
movw %a1h $0x00FF
movw %a2l $0x0FF0
movw %a2h $0xF00F

addw %r1l %a1l %a2l # Expect 0x0EF0 in %r1l
addcw %r1h %a1h %a2h # Expect 0xF10F in %r1h

subw %r1l %a2l %a1l # Expect 0x10F0 in %r1l
subcw %r1h %a2h %a1h # Expect 0xEF0F in %r2h

andw %r1l %a1l %a2l # Expect 0x0F00 in %r1l
andw %r1h %a1h %a2h # Expect 0x000F in %r1h
andw %r2l %a1l %a1h # Expect 0x0000 in %r2l
andw %r2h %a1l %a1l # Expect 0xFF00 in %r2h

orw %r1l %a1l %a2l # Expect 0xFFF0 in %r1l
orw %r1h %a1l %a1h # Expect 0xFFFF in %r1h

xorw %r1l %a1l %a2l # Expect 0xF0F0 in %r1l

invw %r1l %a2l # Expect 0xF00F in %r1l
invw %r1h %a1h # Expect 0xFF00 in %r1l
