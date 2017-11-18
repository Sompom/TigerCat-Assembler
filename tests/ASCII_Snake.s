#
# Author      : Team TigerCat
# Date        : 17 November 2017
# Description : This is a two-player Snake game for the TigerCat architecture
#

# Coding syle suggestions:
# - Use very verbose label names. There is no scoping, so using simple names
#   like "LOOP" will quickly get confusing
# - Indent two spaces after every label for the "body" of that label, where
#   appropriate
# - Try to limit lines to 80 characters
# - Leave very verbose header comments on methods. Be very clear about what
#   and how many arguments to use
# - All registers are CALLER SAVED

# Data type definitions:
# String:
# Array of 8-bit values, read until the 8-bit value 0x0 is encountered
# The shortest possible string would be 0x0000, while the one-length string "a"
# would be 0x6100, and a two-length string "aa" would be, in two consequtive 
# memory addresses, 0x6161 0x0000

# Game data 

#Snake data is represented as an array of this data starting at a player-specific address
#Snake storage data
#{active,  L R U D,  column, row}
#[15]	,  [14:13],  [12:6], [5:0]
#
#Take this data and read it into the game board before printing the board

### Game board

#Board Column and row locations represented by arbitrary address in cellular ram

#enum board_data: {	0 = empty
#					1 = food
#					2 = blue snake
#					3 = orange snake
#					4 = wall}
# 4 bits (even though we only need 3)

# The game board is an array of board_data


# Define your constants here!
VGA_TEXT_BASE_ADDR=0x7FE000 # Stuff written starting here will be drawn to the screen
CONTROLLER_1_READ_ADDR=0x7FDBDB # Read from controller 1 here
CONTROLLER_2_READ_ADDR=0x7FDBDE # Read from controller 2 here
# TODO: game board base address
# End constants

##### Main game loop
#
### Player control
#		read the controllers
#			query the controller module
#			return the two players' directions in the two return regs
#		update head direction
#			If there's no input, the head direction should not be changed
#			Prevent the player from going backwards
#			Go to the player snake addresses and change the head direction
#
### snake changes (dying/eating)
#		Get the head's next position and use it for comparisons
#			Check against the other snake's future head
#				If they're the same: kill both snakes
#
#		collisions with snakes and walls and food
#			scan the game board for overlaps of the heads with anything else
#				Eating logic (collision with food)
#					copy the tail segment backwards one (will get shuffled as normal)
#				Wall collisions
#					kill the snake
#				Snake collisions
#					Kill the snake that has it's head in the other's tail
#			
#		update scores
#			+100 for eating food
#			+1 for game tick
#			=0 for dying
#
### snake movement
#		shuffle along snake segments
#			For each segment of each player
#				increment its position in the direction it's facing
#				change each segment's direction to be the direction of the piece in front of it
#					ignore the head for this
#

### Display
#		Read memory model unit
#			colorize character
#			push to VGA
#	      repeat



# Game data 

#Snake data is represented as an array of this data starting at a player-specific address
#Snake storage data
#{active,  L R U D,  column, row}
#[15]	,  [14:13],  [12:6], [5:0]
#
#Take this data and read it into the game board before printing the board

### Game board

#Board Column and row locations represented by arbitrary address in cellular ram

#enum board_data: {	0 = empty
#					1 = food
#					2 = blue snake
#					3 = orange snake
#					4 = wall}
# 4 bits (even though we only need 3)

# The game board is an array of board_data






# Error Print
# Read a string starting from %arg1 and put it on the screen
# Arguments:
# %arg1: String to print
# %a2l: Colour to print (lower 8 bits)
# Returns:
# void
ERROR_PRINT:
  # Interesting Registers:
  # %s1l - The current and next 8-bit character to write
  # %s1h - The current character to write
  # %arg3 - Destination address into the ASCII section

  movd %arg3 VGA_TEXT_BASE_ADDR # Prepare the text pointer
  slw %a2l $0x8 # Prepare the text colour

  ERROR_PRINT_LOOP_UNTIL_NULL:
    loadw %a1l %s1l
    # Get the first character (upper half of %s1l)
    andw %s1h %s1l $0xFF00 # Mask
    surw %s1h %s1h $0x8    # Move into position
    # Check if that character was null
    cmpw %s1h $0x0
    jmpe ERROR_PRINT_FINISHED
    # Colourize the character
    orw %s1h %s1h %a2l
    # Write the character to the screen
    stow %s1h %a3l
    # Increment the text pointer
    addd %arg3 %arg3 $0x1
    # Get the second character (lower half of %s1l)
    andw %s1h %s1l $0x00FF # Mask
    # Check if that character was null
    cmpw %s1h $0x0
    jmpe ERROR_PRINT_FINISHED
    # Colourize the character
    orw %s1h %s1h %a2l
    # Write the character to the screen
    stow %s1h %a3l
    # Increment the text pointer
    addd %arg3 %arg3 $0x1
    # TODO: Properly loop %arg3 after every 80 characters to write multiple lines
    jmp ERROR_PRINT_LOOP_UNTIL_NULL

  ERROR_PRINT_FINISHED:
    ret
