# This test is not meant to make it through the assembler
# The first pass should report several errors from this file

LOOP:
LOOP: # Double defined label
loop: # Labels must be block capitals
  movw %a1l 0x500 # Syntax Error
  INNNER_LOOP: movw %a2l $0x1 # Label must be on own line
