# This test program writes some lower-case 'a's to the start of the VGA section

movd %ret1 $0x7FE000 # Start of VGA section
stow %r1l $0xFF61 # 0x61 = ASCII 'A', 0xFF means totally white
addw %r1l %r1l $0x1
stow %r1l $0xFF61 # 0x61 = ASCII 'A', 0xFF means totally white
addw %r1l %r1l $0x1
stow %r1l $0xFF61 # 0x61 = ASCII 'A', 0xFF means totally white
addw %r1l %r1l $0x1
stow %r1l $0xFF61 # 0x61 = ASCII 'A', 0xFF means totally white
addw %r1l %r1l $0x1
stow %r1l $0xFF61 # 0x61 = ASCII 'A', 0xFF means totally white
addw %r1l %r1l $0x1
stow %r1l $0xFF61 # 0x61 = ASCII 'A', 0xFF means totally white
addw %r1l %r1l $0x1
stow %r1l $0xFF61 # 0x61 = ASCII 'A', 0xFF means totally white
addw %r1l %r1l $0x1
stow %r1l $0xFF61 # 0x61 = ASCII 'A', 0xFF means totally white
addw %r1l %r1l $0x1
stow %r1l $0xFF61 # 0x61 = ASCII 'A', 0xFF means totally white
addw %r1l %r1l $0x1
stow %r1l $0xFF61 # 0x61 = ASCII 'A', 0xFF means totally white
addw %r1l %r1l $0x1
stow %r1l $0xFF61 # 0x61 = ASCII 'A', 0xFF means totally white
addw %r1l %r1l $0x1
stow %r1l $0xFF61 # 0x61 = ASCII 'A', 0xFF means totally white
addw %r1l %r1l $0x1
stow %r1l $0xFF61 # 0x61 = ASCII 'A', 0xFF means totally white
addw %r1l %r1l $0x1
stow %r1l $0xFF61 # 0x61 = ASCII 'A', 0xFF means totally white
addw %r1l %r1l $0x1
stow %r1l $0xFF61 # 0x61 = ASCII 'A', 0xFF means totally white
addw %r1l %r1l $0x1
stow %r1l $0xFF61 # 0x61 = ASCII 'A', 0xFF means totally white
addw %r1l %r1l $0x1
stow %r1l $0xFF61 # 0x61 = ASCII 'A', 0xFF means totally white
addw %r1l %r1l $0x1
stow %r1l $0xFF61 # 0x61 = ASCII 'A', 0xFF means totally white
addw %r1l %r1l $0x1
stow %r1l $0xFF61 # 0x61 = ASCII 'A', 0xFF means totally white
addw %r1l %r1l $0x1
stow %r1l $0xFF61 # 0x61 = ASCII 'A', 0xFF means totally white
addw %r1l %r1l $0x1
stow %r1l $0xFF61 # 0x61 = ASCII 'A', 0xFF means totally white
addw %r1l %r1l $0x1
