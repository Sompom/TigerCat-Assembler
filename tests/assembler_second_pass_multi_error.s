# This file is not expected to compile, but is instead expected to generate
# lots of errors from the assembler's second pass

jmpt LOOP # Undefined label
movw %a1l $0xFFFFFF # Unencodeable Immediate (Too Large)
movw %arg1 $0xFF # Wrong register for single-word instruction
addd %ret1 %ret1 $0xFFFFFFFF # Unencodeable Immediate
