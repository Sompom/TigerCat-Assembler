# Write some stuff to memory, then verify that the stuff was correctly written

# Relevant registers:
# %arg1: Memory address register. Holds the current address being written
# %arg2: Container for MEMORY_CELLULAR_END

MEMORY_CELLULAR_END=0x7FDBD7 # Stop testing here

movd %arg4 MEMORY_CELLULAR_END

movd %arg1 END # Memory address register - Start writing here
addd %arg1 %arg1 $0x4 # Advance two instructions past the ERROR tag (end of program plus padding)

ZERO:
  addd %arg1 %arg1 $0x1 # Increment address
  # Check if we have written all we need to write
  cmpd %arg1 %arg4
  jmpe DONE
  stow %a1l $0x0 # Write that value to the write address
  jmp ZERO # Jump back to the start

DONE:
  debug # Once we finish, pause the program

END:
  noop # Noop to allow the label to resolve
