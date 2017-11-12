# Write some stuff to memory, then verify that the stuff was correctly written

movd %arg1 ERROR # Memory address register - Start writing here
addw %a1l %a1l $0x4 # Advance two instructions past the ERROR tag
movw %r1l $0x0 # Error code collector thing

LOOP:
  addw %a1l %a1l $0x1 # Increment address
  addcw %a1h %a1h $0x0 # Fake having addd working...
  movw %a2l %a1l # Move the current address to another register
  stow %a1l %a2l # Write that value to the write address
  loadw %a3l %a1l # Load that value back out of memory
  subw %zero %a3l %a2l # Compare what was read to what was written
  jmpz LOOP # Jump back to the start if it was zero

ERROR:
  addw %r1l %r1l $0x1 # Error code... Or whatever
  jmpt LOOP # Go back and try again
