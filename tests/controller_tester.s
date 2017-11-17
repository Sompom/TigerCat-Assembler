# Read the N64 controllers and save their values to registers

# Relevant Registers:
# %ret1 - Controller 1
# %ret2 - Controller 2

CONTROLLER_1_READ_ADDR=0x7FDBDB
CONTROLLER_2_READ_ADDR=0x7FDBDE

movd %arg1 CONTROLLER_1_READ_ADDR
movd %arg2 CONTROLLER_2_READ_ADDR

LOOP:
  stow %a1l %a1l # Writing anything to either controller region sends a reset to the controller controller hardware
  loadd %ret1 %arg1
  loadd %ret2 %arg2
  debug
  jmp LOOP
