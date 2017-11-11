# Test jumps
# The value in %r1l should end up with the value initially stored to %a1l

# Initial Values
movw %a1l $0x500
movw %r1l $0x0

LOOP:
  addw %r1l %r1l $0x1
  subw %a1l %a1l $0x1
  jmpz FIN # Need non-zero condition code! For now, jump into la-la land when finished
  jmpt LOOP # Otherwise, if a1l - 1 was non-zero, jump somewhere sensible

FIN:
  jmpf $0x500 # no-op
