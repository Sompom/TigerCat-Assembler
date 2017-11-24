#
# Author      : Team TigerCat
# Date        : 17 November 2017
# Description : This is a two-player Snake game for the TigerCat architecture
#

# ALWAYS:
jmp INIT #TODO: MAKE INIT

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
#Snake storage data:
# Double-word:
# {unused, active,  L R U D,  column, row}
# [31:17], [16]  ,  [15:14],  [13:6], [5:0]
#
#Take this data and read it into the game board before printing the board

### Game board

#Board Column and row locations represented by arbitrary address in cellular ram

#enum board_data: { 0 = empty
#                   1 = food
#                   2 = blue snake
#                   3 = orange snake
#                   4 = wall}
# 4 bits (even though we only need 3)

# The game board is a 2D array of board_data

# Bits Description:
#        (stop)  --> buttons[0]
#        A       --> buttons[1]
#        B       --> buttons[2]
#        Z       --> buttons[3]
#        Start   --> buttons[4]
#        Up      --> buttons[5]
#        Down    --> buttons[6]
#        Left    --> buttons[7]
#        Right   --> buttons[8]
#        N/A     --> buttons[9]
#        N/A     --> buttons[10]
#        L       --> buttons[11]
#        R       --> buttons[12]
#        C-UP    --> buttons[13]
#        C-DOWN  --> buttons[14]
#        C-Left  --> buttons[15]
#        C-Right --> buttons[16]
#        X-Axis  --> buttons[24:17]
#        Y-Axis  --> buttons[32:25]


# Define your constants here!
VGA_TEXT_BASE_ADDR=0x7FE000 # Stuff written starting here will be drawn to the screen
CONTROLLER_1_READ_ADDR=0x7FDBDB # Read from controller 1 here
CONTROLLER_2_READ_ADDR=0x7FDBDE # Read from controller 2 here

GAME_BOARD_BASE_ADDR=0x3F0000 # Game board starts here
GAME_BOARD_LENGTH=0x1E00
# Game Board is a 128x60 array of words, so goes until 0x3F1E00
# Note that we could pack the 4-bit ints 4 per word, but that would be a pain
SNAKE_1_BASE_ADDR=0x3F2000 # Player 1 snake starts here
# Snakes are, at the very largest, < 4096, so the end of snake 1 is 0x3F3FFF
SNAKE_2_BASE_ADDR=0x3F4000 # Player 2 snake starts here
# Snakes are, at the very largest, < 4096, so the end of snake 2 is 0x3F5FFF
SNAKE_LENGTH=0x2000 # Both snakes are the same length
# 0x2000 = 4192 * 2 (max snake length * two words per segment)

GAME_BOARD_NUM_ROWS=0x3C # 60 rows
GAME_BOARD_NUM_COLUMNS=0x50 # 80 columns
GAME_BOARD_DEAD_SPACE=0x30 # The game board is an array 128 wide, but only 80 of that is visible

GAME_BOARD_SIDE_BORDERS=0x2 # Width of walls on the left and right side
GAME_BOARD_BOTTOM_BORDER=0x2 # Width of walls on the bottom of the game board
GAME_BOARD_TOP_BORDER=0x3 # Width of walls on the top of the game board

GAME_BOARD_EMPTY=0x0
GAME_BOARD_FOOD=0x1
GAME_BOARD_BLUE_SNAKE=0x2
GAME_BOARD_ORANGE_SNAKE=0x3
GAME_BOARD_WALL=0x4

EMPTY_COLOUR=0x00         # Black
FOOD_COLOUR=0xFF          # White
BLUE_SNAKE_COLOUR=0x3     # Blue
ORANGE_SNAKE_COLOUR=0xF0  # Orange
WALL_COLOUR=0x6F          # Teal

# All tiles are the same ASCII value, but are individually defined in case we
# want to do something cleverer later
EMPTY_ASCII_VALUE=0x80
FOOD_ASCII_VALUE=0x80
SNAKE_ASCII_VALUE=0x80
WALL_ASCII_VALUE=0x80

GAME_TICK_VALUE=0x100 #time in between ticks
# TODO: game board base address
# End constants

##### Main game loop
#
### Player control
#     read the controllers
#       query the controller module
#       return the two players' directions in the two return regs
#     update head direction
#       If there's no input, the head direction should not be changed
#       Prevent the player from going backwards
#       Go to the player snake addresses and change the head direction
#
### snake changes (dying/eating)
#    Get the head's next position and use it for comparisons
#      Check against the other snake's future head
#        If they're the same: kill both snakes
#
#    collisions with snakes and walls and food
#      scan the game board for overlaps of the heads with anything else
#        Eating logic (collision with food)
#          copy the tail segment backwards one (will get shuffled as normal)
#        Wall collisions
#          kill the snake
#        Snake collisions
#          Kill the snake that has it's head in the other's tail
#      
#    update scores
#      +100 for eating food
#      +1 for game tick
#      =0 for dying
#
### snake movement
#     shuffle along snake segments
#       For each segment of each player
#         increment its position in the direction it's facing
#         change each segment's direction to be the direction of the piece in front of it
#           ignore the head for this
#

### Display
#    Read memory model unit
#      colorize character
#      push to VGA
#        repeat


###### START ASSEMBLY ######

# Init
# Initializes the game board and the two player snakes
# Arguments:
# None
# Return:
# void
INIT:
  # Write zeros to the entire game board to mark everything as empty
  call EMPTY_GAME_BOARD

  # Write zeros to the entire snake 1 to mark every segment as inactive
  movd %arg1 SNAKE_1_BASE_ADDR
  movd %arg2 SNAKE_LENGTH
  movw %a3l $0x0
  call MEMCPY_WORD
  # Write zeros to the entire snake 2 to mark every segment as inactive
  movd %arg1 SNAKE_2_BASE_ADDR
  movd %arg2 SNAKE_LENGTH
  movw %a3l $0x0
  call MEMCPY_WORD
  
  # Create brand-new baby snakes
  # Put the walls onto the in-memory game board
  call GAME_BOARD_ADD_WALLS
  # Put the snakes onto the in-memory game board
  # Randomly generate a food location and put it on the board
  call COPY_GAME_BOARD_TO_VGA
  jmp MAIN_GAME_LOOP
#End INIT


# memcpy
# Write a range of memory with a constant value
# If arg2 is zero, this method will misbehave
# Arguments:
# %arg1: Base address
# %arg2: Number of WORDs to write
# %a3l: Constant value to write
# Return:
# void
MEMCPY_WORD:
  addd %arg4 %arg1 %arg2 # Setup arg4 as the end address
  MEMCPY_WORD_LOOP:
    stow %a1l %a3l
    addd %arg1 %arg1 $0x1
    cmpd %arg1 %arg4
    jmpa MEMCPY_WORD_LOOP # arg4 >? arg1
    ret
  #End MEMCPY_WORD_LOOP
#End MEMCPY_WORD


# Empty Game Board
# Clear the entire game board
# Arguments:
# None
# Return:
# void
EMPTY_GAME_BOARD:
  movd %arg1 GAME_BOARD_BASE_ADDR
  movd %arg2 GAME_BOARD_LENGTH
  movw %a3l $0x0
  call MEMCPY_WORD
  ret
# End EMPTY_GAME_BOARD


# Game Board Add Walls
# Put the walls on the game board
# Uses the widths defined as constants
# Arguments:
# None
# Return:
# void
GAME_BOARD_ADD_WALLS:
  movd %arg1 GAME_BOARD_BASE_ADDR # Load up the base address
  # Put walls at the top
  
  # Use this counter to keep track of the number of top borders we need to draw
  movw %r1l GAME_BOARD_TOP_BORDER
  
  GAME_BOARD_ADD_WALLS_TOP_LOOP:
    # Since the walls at the top are contiguous, we can just write a large region...
    movd %arg2 GAME_BOARD_NUM_COLUMNS
    movw %a3l GAME_BOARD_WALL
    # Save registers for function call
    pushd %arg1
    pushw %r1l
    call MEMCPY_WORD
    popw %r1l
    popd %arg1
    # Increment the base address to the next row
    # Each row is 128 words long = 0x80
    addd %arg1 %arg1 $0x80
    subw %r1l %r1l $0x1
    cmpw %r1l $0x0
    jmpb GAME_BOARD_ADD_WALLS_TOP_LOOP # $0x0 <? %r1l
  # End GAME_BOARD_ADD_WALLS_TOP_LOOP

  # Put walls on the sides
  # After finishing the top walls loop,
  # %arg1 is point to the first point in the next row
  # Number of side rows is the total number of rows minus the top and bottom border
  movw %r1l GAME_BOARD_NUM_ROWS
  subw %r1l %r1l GAME_BOARD_TOP_BORDER
  subw %r1l %r1l GAME_BOARD_BOTTOM_BORDER
  movw %a3l GAME_BOARD_WALL
  # Empty space in the middle of the board is its width minus two side borders
  movw %a3h GAME_BOARD_NUM_COLUMNS
  subw %a3h %a3h GAME_BOARD_SIDE_BORDERS
  subw %a3h %a3h GAME_BOARD_SIDE_BORDERS
  GAME_BOARD_ADD_WALLS_SIDE_LOOP:
    # Left border
    # Hard-coded width of two. Could be fixed with another loop.
    stow %a1l %a3l
    addd %arg1 %arg1 $0x1
    stow %a1l %a3l
    addd %arg1 %arg1 $0x1
    # Move across the middle
    addw %a1l %a1l %a3h
    addcw %a1h %a1h %zero
    # Right border. Also hard-coded width, same fix required
    stow %a1l %a3l
    addd %arg1 %arg1 $0x1
    stow %a1l %a3l
    addd %arg1 %arg1 $0x1
    # Move to the next line of the game board
    addd %arg1 %arg1 GAME_BOARD_DEAD_SPACE
    # Decrement the row counter and loop
    subw %r1l %r1l $0x1
    cmpw %r1l $0x0
    jmpb GAME_BOARD_ADD_WALLS_SIDE_LOOP # $0x0 <? %r1l
  # End GAME_BOARD_ADD_WALLS_SIDE_LOOP

  # Put walls at the bottom
  # Use this counter to keep track of the number of top borders we need to draw
  movw %r1l GAME_BOARD_BOTTOM_BORDER
  GAME_BOARD_ADD_WALLS_BOTTOM_LOOP:
    # Since the walls at the bottom are contiguous, we can just write a large region...
    movd %arg2 GAME_BOARD_NUM_COLUMNS
    movw %a3l GAME_BOARD_WALL
    # Save registers for function call
    pushd %arg1
    pushw %r1l
    call MEMCPY_WORD
    popw %r1l
    popd %arg1
    # Increment the base address to the next row
    # Each row is 128 words long = 0x80
    addd %arg1 %arg1 $0x80
    subw %r1l %r1l $0x1
    cmpw %r1l $0x0
    jmpb GAME_BOARD_ADD_WALLS_BOTTOM_LOOP # $0x0 <? %r1l
  # End GAME_BOARD_ADD_WALLS_BOTTOM_LOOP
  ret
#End GAME_BOARD_ADD_WALLS


# Copy Game Board to VGA
# Convert the in-memory game board to ASCII and push it to the VGA
# Arguments:
# None
# Return:
# void
COPY_GAME_BOARD_TO_VGA:
  # Interesting Registers:
  # %r1l - The final, colourized piece
  # %r1h - Register used to load the value from the game board
  # %r2l - Used to hold the colour for colourizing the piece
  # %arg1 - Store the address we are working on in the game board
  # %arg2 - Store the next address to write in the VGA
  # %arg4 - Store the end address of the game board, to know when we are done
  movd %arg1 GAME_BOARD_BASE_ADDR # Load up the base address
  movd %arg2 VGA_TEXT_BASE_ADDR
  addd %arg4 %arg1 GAME_BOARD_LENGTH # Load up the end address
  COPY_GAME_BOARD_TO_VGA_LOOP:
    movw %r1l $0x0 # %r1l will be the colourized piece to draw
    loadw %r1h %a1l # Load the next board piece
    cmpw %r1h GAME_BOARD_EMPTY
    jmpe COPY_GAME_BOARD_TO_VGA_PREPARE_EMPTY
    cmpw %r1h GAME_BOARD_FOOD
    jmpe COPY_GAME_BOARD_TO_VGA_PREPARE_FOOD
    cmpw %r1h GAME_BOARD_BLUE_SNAKE
    jmpe COPY_GAME_BOARD_TO_VGA_PREPARE_BLUE_SNAKE
    cmpw %r1h GAME_BOARD_ORANGE_SNAKE
    jmpe COPY_GAME_BOARD_TO_VGA_PREPARE_ORANGE_SNAKE
    cmpw %r1h GAME_BOARD_WALL
    jmpe COPY_GAME_BOARD_TO_VGA_PREPARE_WALL

    COPY_GAME_BOARD_TO_VGA_PREPARE_EMPTY:
      movw %r2l EMPTY_COLOUR
      movw %r1l EMPTY_ASCII_VALUE
      jmp COPY_GAME_BOARD_TO_VGA_DOIT

    COPY_GAME_BOARD_TO_VGA_PREPARE_FOOD:
      # TODO: Implement
      jmp COPY_GAME_BOARD_TO_VGA_DOIT

    COPY_GAME_BOARD_TO_VGA_PREPARE_BLUE_SNAKE:
      # TODO: Implement
      jmp COPY_GAME_BOARD_TO_VGA_DOIT

    COPY_GAME_BOARD_TO_VGA_PREPARE_ORANGE_SNAKE:
      # TODO: Implement
      jmp COPY_GAME_BOARD_TO_VGA_DOIT

    COPY_GAME_BOARD_TO_VGA_PREPARE_WALL:
      movw %r2l WALL_COLOUR
      movw %r1l WALL_ASCII_VALUE
      jmp COPY_GAME_BOARD_TO_VGA_DOIT

    COPY_GAME_BOARD_TO_VGA_DOIT:
      slw %r2l %r2l $0x8 # Shift the colour into position
      orw %r1l %r1l %r2l
      # Do the actual copy
      stow %a2l %r1l
    # End COPY_GAME_BOARD_TO_VGA_DOIT
    addd %arg1 %arg1 $0x1
    addd %arg2 %arg2 $0x1
    cmpd %arg4 %arg1
    jmpb COPY_GAME_BOARD_TO_VGA_LOOP # arg1 <? arg4
  # End COPY_GAME_BOARD_TO_VGA_LOOP
  ret
# End COPY_GAME_BOARD_TO_VGA


#### Main Game Loop
MAIN_GAME_LOOP:
  ## game tick
  movd %arg1 GAME_TICK_VALUE
  GAME_TICK_DELAY:
    subd %arg1 %arg1 $0x100
    cmpd %arg1 $0x0
    jmpg GAME_TICK_DELAY
  # end GAME_TICK_DELAY

  ### Player control
  #   read the controllers
  #     query the controller module
  #     return the two players' directions in the two return regs
  #   update head direction
  #     If there's no input, the head direction should not be changed
  #     Prevent the player from going backwards
  #     Go to the player snake addresses and change the head direction 

  ## Update head direction
  movd %ret1 %arg1 #player 1
  movd %ret2 %arg2 #player 2
  call UPDATE_SNAKE_HEADS




  jmp MAIN_GAME_LOOP
# end MAIN_GAME_LOOP



# Controller Read
# Read from both controllers, put their outputs into %ret1 and %ret2
# Arguments:
# None
# Return:
# %ret1: Controller 1 value
# %ret2: Controller 2 value
CONTROLLER_READ:
  movd %arg1 CONTROLLER_1_READ_ADDR
  movd %arg2 CONTROLLER_2_READ_ADDR

  stow %a1l %a1l # Writing anything to either controller region sends a reset 
                 # to the controller hardware (Should, anyway. Is 
                 # currently broken in hardware)
  loadd %ret1 %arg1
  loadd %ret2 %arg2
  ret


# Update snake heads
# Take controller outputs, and set the direction of the snake heads
# appropriately
# Arguments:
# None
# Return:
# %ret1: Controller 1 value
# %ret2: Controller 2 value
UPDATE_SNAKE_HEADS:
  # Mask off the dpad bits
  # Up   --> buttons[5]
  # Down --> buttons[6]
  # Left --> buttons[7]
  # Right--> buttons[8]
 
  ret



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
  slw %a2l %a2l $0x8 # Prepare the text colour

  ERROR_PRINT_LOOP_UNTIL_NULL:
    loadw %s1l %a1l
    # Get the first character (upper half of %s1l)
    andw %s1h %s1l $0xFF00 # Mask
    surw %s1h %s1h $0x8    # Move into position
    # Check if that character was null
    cmpw %s1h $0x0
    jmpe ERROR_PRINT_FINISHED
    # Colourize the character
    orw %s1h %s1h %a2l
    # Write the character to the screen
    stow %a3l %s1h
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
    stow %a3l %s1h
    # Increment the text pointer
    addd %arg3 %arg3 $0x1
    # TODO: Properly loop %arg3 after every 80 characters to write multiple lines
    addd %arg1 %arg1 $0x1 # Advance the input pointer
    call ERROR_PRINT_LOOP_UNTIL_NULL

  ERROR_PRINT_FINISHED:
    ret
