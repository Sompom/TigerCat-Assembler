# Write some stuff to memory, then verify that the stuff was correctly written

# Relevant registers:
# %r1l: Error count. If > 0, something bad has happened
# %r2l: Test mode. See inline comments. If the program stops with errors, check
#       this to see what pattern was being written
# %arg1: Memory address register. Holds the current address being written
# %arg2: Container for MEMORY_CELLULAR_END

MEMORY_CELLULAR_END=0x7FDBD7 # Stop testing here

movd %arg4 MEMORY_CELLULAR_END

movd %arg1 END # Memory address register - Start writing here
addw %a1l %a1l $0x4 # Advance two instructions past the ERROR tag (end of program plus padding)

movw %r1l $0x0 # Error collector. If > 0, we read something different than we wrote. That is bad.

movw %r1h $0x1 # Mode = 1 -> Sequential write
SEQUENTIAL:
  addd %arg1 %arg1 $0x1 # Increment address
  # Check if we have written all we need to write
  cmpd %arg1 %arg4
  jmpe SEQUENTIAL_DONE
  movw %a2l %a1l # Move the current address to another register
  stow %a1l %a2l # Write that value to the write address
  loadw %a3l %a1l # Load that value back out of memory
  cmpw %a3l %a2l # Compare what was read to what was written
  jmpz SEQUENTIAL # Jump back to the start if we read and wrote the same thing
  addw %r1l %r1l $0x1
  debug # If we read the wrong value, halt the program
  jmp SEQUENTIAL # Let the operator continue anyway

SEQUENTIAL_DONE:
  debug # Once we finish this mode, pause the program

END:
  jmpf $0x0 # Noop to allow the label to resolve
