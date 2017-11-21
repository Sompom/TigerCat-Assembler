# Reads the controllers and writes their values to the VGA as ASCII

DUMMY_LOOP:
  call CONTROLLER_READ

  # Choose some random memory address
  movd %arg1 $0xD000

  stow %a1l $0x4865 # "He"
  addd %arg1 %arg1 $0x1
  stow %a1l $0x6C6C # "ll"
  addd %arg1 %arg1 $0x1
  stow %a1l $0x6F20 # "o "
  addd %arg1 %arg1 $0x1
  stow %a1l $0x576F # "Wo"
  addd %arg1 %arg1 $0x1
  stow %a1l $0x726C # "rl"
  addd %arg1 %arg1 $0x1
  stow %a1l $0x6420 # "d "
  addd %arg1 %arg1 $0x1

  # Append the controller data
  addw %r1l %r1l $0x3030 # Add ASCII '0' to get a number on the print
  addw %r1h %r1h $0x3030 # Add ASCII '0' to get a number on the print
  addw %r2h %r2h $0x3030 # Add ASCII '0' to get a number on the print
  addw %r2l %r2l $0x3030 # Add ASCII '0' to get a number on the print

  stow %a1l %r1h
  addd %arg1 %arg1 $0x1
  stow %a1l %r1l
  addd %arg1 %arg1 $0x1
  stow %a1l %r2h
  addd %arg1 %arg1 $0x1
  stow %a1l %r2l
  addd %arg1 %arg1 $0x1
  stow %a1l $0x0000 # "\0\0"

  # Call Error Print
  movd %arg1 $0xD000
  movw %a2l $0xFF
  call ERROR_PRINT
  jmp DUMMY_LOOP
