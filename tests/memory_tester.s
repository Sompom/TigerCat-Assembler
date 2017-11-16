# Write some stuff to memory, then verify that the stuff was correctly written

# Relevant registers:
# %r1l: Error count. If > 0, something bad has happened
# %r2l: Test mode. See inline comments. If the program stops with errors, check
#       this to see what pattern was being written
# %arg1: Memory address register. Holds the current address being written

movd %arg1 END # Memory address register - Start writing here
addw %a1l %a1l $0x4 # Advance two instructions past the ERROR tag (end of program plus padding)

movw %r1l $0x0 # Error collector. If > 0, we read something different than we wrote. That is bad.

movw %r1h $0x1 # Mode = 1 -> Sequential write
SEQUENTIAL:
  addw %a1l %a1l $0x1 # Increment address
  addcw %a1h %a1h $0x0 # Fake having addd working... Increment the upper half address register.
  # Check if we have written all we need to write
  subw %zero %a1h $0x7F
  jmpe SEQUENTIAL_NEARLY_FINISHED

  SEQUENTIAL_KEEP_GOING:
  movw %a2l %a1l # Move the current address to another register
  stow %a1l %a2l # Write that value to the write address
  loadw %a3l %a1l # Load that value back out of memory
  subw %zero %a3l %a2l # Compare what was read to what was written
  jmpz SEQUENTIAL # Jump back to the start if we read and wrote the same thing
  addw %r1l %r1l $0x1
  debug # If we read the wrong value, halt the program
  jmp SEQUENTIAL # Let the operator continue anyway

  # This partial state needed until we have double-word compare working....
  SEQUENTIAL_NEARLY_FINISHED:
    cmpw %a1l $0xFFFF
    jmpe SEQUENTIAL_DONE
    jmp SEQUENTIAL_KEEP_GOING

SEQUENTIAL_DONE:
  debug # Once we finish this mode, pause the program

END:
  jmpf $0x0 # Noop to allow the label to resolve
