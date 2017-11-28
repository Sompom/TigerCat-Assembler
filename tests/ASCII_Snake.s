#
# Author      : Team TigerCat
# Date        : 27 November 2017
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
# would be 0x6100, and a two-length string "aa" would be, in two consecutive 
# memory addresses, 0x6161 0x0000

# Game data 

# Snake data is represented as an array of this data starting at a player-specific address
# Snake storage data:
# Single-word:
# {active,  U D L R,  column, row}
#  [15]  ,  [14:13],  [12:6], [5:0]
#
# The row and column are defined from the top left corner of the SCREEN, meaning
# (for example) coordinate 0,0 (top left) and 79,59 (bottom right) are occupied by walls
#
# Take the snake data and read it into the game board before printing the board

### Game board

# Board Column and row locations represented by arbitrary address in cellular ram

# enum board_data: { 0 = empty
#                    1 = food
#                    2 = blue snake
#                    3 = orange snake
#                    4 = wall}
# 4 bits (even though we only need 3)

# The game board is a 2D array of board_data (128x60 array of words)
#  - 1 enum per word (we have sooooo much ram)

# Controller bits Description:
#        A       --> buttons[0]
#        B       --> buttons[1]
#        Z       --> buttons[2]
#        Start   --> buttons[3]
#        Up      --> buttons[4]
#        Down    --> buttons[5]
#        Left    --> buttons[6]
#        Right   --> buttons[7]
#        N/A     --> buttons[8]
#        N/A     --> buttons[9]
#        L       --> buttons[10]
#        R       --> buttons[11]
#        C-UP    --> buttons[12]
#        C-DOWN  --> buttons[13]
#        C-Left  --> buttons[14]
#        C-Right --> buttons[15]
#        X-Axis  --> buttons[23:16]
#        Y-Axis  --> buttons[31:24]


# Define your constants here!
VGA_TEXT_BASE_ADDR=0x7FE000 # Stuff written starting here will be drawn to the screen
CONTROLLER_1_READ_ADDR=0x7FDBDB # Read from controller 1 here
CONTROLLER_2_READ_ADDR=0x7FDBDE # Read from controller 2 here

GAME_BOARD_BASE_ADDR=0x3F0000 # Game board starts here
GAME_BOARD_LENGTH=0x1E00
# Game Board is a 128x60 array of words, so goes until 0x3F1E00
# Note that we could pack the 4-bit ints 4 per word, but that would be a pain
# Store the coordinates of the food, using the same struct as a snake, here
FOOD_ADDRESS=0x3F1E000
PLAYER_1_SCORE=0x3F1E001 # Player 1's score, single word integer (global variable)
PLAYER_2_SCORE=0x3F1E002 # Player 2's score, single word integer (global variable)
SNAKE_1_BASE_ADDR=0x3F2000 # Player 1 snake starts here
# Snakes are, at the very largest, < 4096, so the end of snake 1 is 0x3F3FFF
SNAKE_2_BASE_ADDR=0x3F3000 # Player 2 snake starts here
# Snakes are, at the very largest, < 4096, so the end of snake 2 is 0x3F5FFF
SNAKE_LENGTH=0x1000 # Both snakes are the same length
# 0x2000 = 4192 * 2 (max snake length * two words per segment)

SNAKE_DIRECTION_LEFT=0x2
SNAKE_DIRECTION_DOWN=0x1
SNAKE_DIRECTION_RIGHT=0x3
SNAKE_DIRECTION_UP=0x0

SNAKE_ACTIVE=0x1
SNAKE_INACTIVE=0x0

GAME_BOARD_NUM_ROWS=0x3C # 60 rows
GAME_BOARD_NUM_COLUMNS=0x50 # 80 columns
GAME_BOARD_DEAD_SPACE=0x30 # The game board is an array 128 wide, but only 80 of that is visible

GAME_BOARD_SIDE_BORDERS=0x2 # Width of walls on the left and right side
GAME_BOARD_BOTTOM_BORDER=0x2 # Width of walls on the bottom of the game board
GAME_BOARD_TOP_BORDER=0x3 # Width of walls on the top of the game board

GAME_BOARD_EMPTY=0x0
GAME_BOARD_FOOD=0x1
GAME_BOARD_PLAYER_1_SNAKE=0x2
GAME_BOARD_PLAYER_2_SNAKE=0x3
GAME_BOARD_WALL=0x4

EMPTY_COLOUR=0x00         # Black
FOOD_COLOUR=0xFF          # White
BLUE_SNAKE_COLOUR=0x3     # Blue
ORANGE_SNAKE_COLOUR=0xF0  # Orange
WALL_COLOUR=0x6F          # Teal

# All tiles are the same ASCII value, but are individually defined in case we
# want to do something cleverer later
# TODO: do something clever
EMPTY_ASCII_VALUE=0x80
FOOD_ASCII_VALUE=0x80
SNAKE_ASCII_VALUE=0x80
WALL_ASCII_VALUE=0x80

GAME_TICK_VALUE=0x3FFFF # time in between ticks
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
  # Write zeros to the entire snake 1 to mark every segment as inactive
  movd %arg1 SNAKE_1_BASE_ADDR
  call NULLIFY_SNAKE

  # Write zeros to the entire snake 2 to mark every segment as inactive
  movd %arg1 SNAKE_2_BASE_ADDR
  call NULLIFY_SNAKE

  # Create brand-new baby snakes
  call SPAWN_SNAKE_1
  call SPAWN_SNAKE_2

  # Randomly generate a food location and put it on the board
  call GENERATE_FOOD

  call UPDATE_GAME_BOARD
  
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


# Snake Segment Unpack
# Unpacks the row, column, direction, and existence from the passed snake segment
# Arguments:
# %a1l: Snake segment to unpack
# Return:
# %r1l: row
# %r1h: column
# %r2l: direction
# %r2h: active bit
SNAKE_SEGMENT_UNPACK:
  movw %r1l $0x3F # Mask for the row
  movw %r1h $0x1FC0 # Mask for the column
  movw %r2l $0x6000 # Mask for the direction
  movw %r2h $0x8000 # Mask for the active bit
  andw %r1l %a1l %r1l # Load the row
  # No need to shift the row
  andw %r1h %a1l %r1h # Load the colummn
  surw %r1h %r1h $0x6 # Move the column to the lower bits
  andw %r2l %a1l %r2l # Load the direction
  surw %r2l %r2l $0xD # Move the direction to the lower bits
  andw %r2h %a1l %r2h # Load the active bit
  surw %r2h %r2h $0xF # Move the active bit to the lowest bit
  ret
# End SNAKE_SEGMENT_UNPACK


# Snake Segment Pack
# Pack the row, column, direction, and active bit into a snake segment
# Arguments:
# %a1l: row
# %a1h: column
# %a2l: direction
# %a2h: active bit
# Return:
# %r1l: The packed snake segment
SNAKE_SEGMENT_PACK:
  slw %a1h %a1h $0x6 # Move the column into position
  slw %a2l %a2l $0xD # Move the direction into position
  slw %a2h %a2h $0xF # Move the active bit into position

  movw %r1l %a1l # Add the row
  orw %r1l %r1l %a1h # Add the column
  orw %r1l %r1l %a2l # Add the direction
  orw %r1l %r1l %a2h # Add the active bit
  ret
# End SNAKE_SEGMENT_PACK


# Nullify Snake
# Write zero to an entire snake, therefore making all of its segments inactive
# Arguments:
# %arg1: The base address of the snake to nullify
# Return:
# void
NULLIFY_SNAKE:
  movd %arg2 SNAKE_LENGTH
  movw %a3l $0x0
  call MEMCPY_WORD
  ret
# End NULLIFY_SNAKE


# Spawn Snake 1
# Puts a new snake in its spawn location
# Arguments:
# None
# Return:
# void
SPAWN_SNAKE_1:
  # Prepare the coordinates of the first snake's head
  # It should be placed in the top left of the board, after the walls,
  # one square down

  # Prepare the row, which should be the second square past the top wall
  movw %a1l $0x0
  addw %a1l %a1l GAME_BOARD_TOP_BORDER
  addw %a1l %a1l $0x1

  # Prepare the column, which should be the first square past the side wall
  movw %a1h $0x0
  addw %a1h %a1h GAME_BOARD_SIDE_BORDERS

  # Prepare the direction - Down
  movw %a2l SNAKE_DIRECTION_DOWN

  # Prepare the active bit - Active
  movw %a2h SNAKE_ACTIVE

  call SNAKE_SEGMENT_PACK

  # Write the new segment to the snake
  movd %arg3 SNAKE_1_BASE_ADDR
  stow %a3l %r1l

  # Now give the snake one tail segment

  # Prepare the row, which should be the first square past the top wall
  movw %a1l $0x0
  addw %a1l %a1l GAME_BOARD_TOP_BORDER

  # Prepare the column, which should be the first square past the side wall
  movw %a1h $0x0
  addw %a1h %a1h GAME_BOARD_SIDE_BORDERS

  # Prepare the direction - Down
  movw %a2l SNAKE_DIRECTION_DOWN

  # Prepare the active bit - Active
  movw %a2h SNAKE_ACTIVE

  call SNAKE_SEGMENT_PACK

  # Write the new segment to the snake
  movd %arg3 SNAKE_1_BASE_ADDR
  addd %arg3 %arg3 $0x1
  stow %a3l %r1l

  ret
# End SPAWN_SNAKE_1


# Spawn Snake 2
# Puts a new snake in its spawn location
# Arguments:
# None
# Return:
# void
SPAWN_SNAKE_2:
  # Prepare the coordinates of the second snake's head
  # It should be placed in the bottom right of the board, before the walls,
  # one square up

  # Prepare the row, which should be the second square before the bottom wall
  movw %a1l GAME_BOARD_NUM_ROWS
  subw %a1l %a1l $0x1 # NUM_ROWS is 'one-indexed', while our board starts at 0
  subw %a1l %a1l GAME_BOARD_BOTTOM_BORDER
  subw %a1l %a1l $0x1

  # Prepare the column, which should be the last square before the side wall
  movw %a1h GAME_BOARD_NUM_COLUMNS
  subw %a1h %a1h $0x1 # NUM_COLUMNS is 'one-indexed', while our board starts at 0
  subw %a1h %a1h GAME_BOARD_SIDE_BORDERS

  # Prepare the direction - Down
  movw %a2l SNAKE_DIRECTION_UP

  # Prepare the active bit - Active
  movw %a2h SNAKE_ACTIVE

  call SNAKE_SEGMENT_PACK

  # Write the new segment to the snake
  movd %arg3 SNAKE_2_BASE_ADDR
  stow %a3l %r1l

  # Now give the snake one tail segment

  # Prepare the row, which should be the last square before the bottom wall
  movw %a1l GAME_BOARD_NUM_ROWS
  subw %a1l %a1l $0x1 # NUM_ROWS is 'one-indexed', while our board starts at 0
  subw %a1l %a1l GAME_BOARD_BOTTOM_BORDER

  # Prepare the column, which should be the last square before the side wall
  movw %a1h GAME_BOARD_NUM_COLUMNS
  subw %a1h %a1h $0x1 # NUM_COLUMNS is 'one-indexed', while our board starts at 0
  subw %a1h %a1h GAME_BOARD_SIDE_BORDERS

  # Prepare the direction - Up
  movw %a2l SNAKE_DIRECTION_UP

  # Prepare the active bit - Active
  movw %a2h SNAKE_ACTIVE

  call SNAKE_SEGMENT_PACK

  # Write the new segment to the snake
  movd %arg3 SNAKE_2_BASE_ADDR
  addd %arg3 %arg3 $0x1
  stow %a3l %r1l

  ret
# End SPAWN_SNAKE_2

# Generate Food
# Replace the current in-memory food with a new one
# This method will slightly skew the food towards the right side, and significantly 
# skew it towards the bottom. Too bad, so sad.
# Uses the same struct as a snake, ignoring everything except the row and column
# Single-word:
# {active,  U D L R,  column, row}
#  [15]  ,  [14:13],  [12:6], [5:0]
# Arguments:
# None
# Return:
# void
GENERATE_FOOD:
  movw %a1l %rand # Randomly generate a row and column
  call SNAKE_SEGMENT_UNPACK
  # TODO: Actual checking for food row in-bounds-ness
  # For now, bound the food by truncating it to 5 bits, then adding enough
  # to make sure it isn't in the top wall
  andw %r1l %r1l $0x1F
  addw %r1l %r1l GAME_BOARD_TOP_BORDER

  # Ensure we have not rolled off the left border
  cmpw %r1h GAME_BOARD_SIDE_BORDERS
  jmpbe GENERATE_FOOD_LEFT_COLUMN_OKAY # SIDE_BORDER <? column
    # If we are off the left side, move back on
    addw %r1h %r1h GAME_BOARD_SIDE_BORDERS
    jmp GENERATE_FOOD_COLUMN_FINISHED
    GENERATE_FOOD_LEFT_COLUMN_OKAY:
    # Check if the column is now off the right border
    movw %a1l GAME_BOARD_NUM_COLUMNS # Compare against the total number of columns...
    subw %a1l %a1l GAME_BOARD_SIDE_BORDERS # ...minus the number of walls on the right
    cmpw %a1l %r1h # Check if this food's column is in-bounds on the right
    jmpb GENERATE_FOOD_COLUMN_FINISHED # r1h <? a1l
    # Otherwise we were out-of-bounds on the right, push back in-bounds
    # Since this is a 6-bit number, the max values is 63
    # 'In Bounds' is the difference between the actual board width (59) and the
    # maximum value, minus two wall sections
    # I.e., we need to subtract (2 + (63 - 59)) = 6 to ensure we are in-bounds
    subw %r1h %r1h $0x6
  GENERATE_FOOD_COLUMN_FINISHED:
  # Pack the row and column together
  movd %arg1 %ret1
  call SNAKE_SEGMENT_PACK
  # Write to memory
  movd %arg2 FOOD_ADDRESS
  stow %a2l %r1l
  ret
# End GENERATE_FOOD


# Convert Coordinates to Game Board Address
# Convert a row and column into an address in the game board array
# Arguments:
# %a1l: row
# %a1h: column
# Return:
# %ret1: The address in the game board
CONVERT_COORDINATES_TO_GAME_BOARD_ADDRESS:
  movd %ret1 GAME_BOARD_BASE_ADDR
  # Each row of the game board is 128 addresses long
  # Therefore, to get to the proper coordinate, multiply the row by 128
  # This is equivalent to left shifting by 7
  slw %a1l %a1l $0x7
  addw %r1l %r1l %a1l
  addcw %r1h %r1h %zero
  # Then, simply add the column
  addw %r1l %r1l %a1h
  addcw %r1h %r1h %zero
  ret
# End CONVERT_COORDINATES_TO_GAME_BOARD_ADDRESS


# Convert Coordinates to Game Board Entity 
# Convert a row and column into en entity in the game board : empty, wall, food, or snake
# Arguments:
# %a1l: row
# %a1h: column
# Return:
# %r1l: The entity at the given cooridinates
CONVERT_COORDINATES_TO_GAME_BOARD_ENTITY:
  # Pass the coordinates on
  call CONVERT_COORDINATES_TO_GAME_BOARD_ADDRESS
  # Game board address is now in %ret1

  loadw %r1l %ret1 #load and replace since we don't need it anymore
  ret
# End CONVERT_COORDINATES_TO_GAME_BOARD_ENTITY


# Update Game Board
# Read all the other in-memory structs to make the game board
# reflect the current state
# Arguments:
# None
# Return:
# void
UPDATE_GAME_BOARD:
  # Write zeros to the entire game board to mark everything as empty
  call EMPTY_GAME_BOARD
  # Put the walls onto the in-memory game board
  call GAME_BOARD_ADD_WALLS
  call GAME_BOARD_ADD_FOOD
  # Put the snakes onto the in-memory game board
  call GAME_BOARD_ADD_SNAKES
  ret
# End UPDATE_GAME_BOARD


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


# Game Board Add Snakes
# Put the snakes on the in-memory game board
# Arguments:
# None
# Return:
# void
GAME_BOARD_ADD_SNAKES:
  # Copy snake 1 first
  movd %arg1 SNAKE_1_BASE_ADDR
  movw %a2l GAME_BOARD_PLAYER_1_SNAKE
  call GAME_BOARD_ADD_SNAKES_HELPER
  # Then copy snake 2
  movd %arg1 SNAKE_2_BASE_ADDR
  movw %a2l GAME_BOARD_PLAYER_2_SNAKE
  call GAME_BOARD_ADD_SNAKES_HELPER
  ret
# End GAME_BOARD_ADD_SNAKES

# Game Board Add Snakes Helper
# Do the actual work of putting one snake on the board
# Arguments:
# %arg1 - Base address of the snake to add to the board
# %a2l - Game board marker to use (GAME_BOARD_PLAYER_1_SNAKE or GAME_BOARD_PLAYER_2_SNAKE)
# Return:
# void
GAME_BOARD_ADD_SNAKES_HELPER:
  pushd %arg1 # Save the current address into the snake
  pushw %a2l  # Save the game board marker 
  loadw %a1l %a1l # Load the next segment
  call SNAKE_SEGMENT_UNPACK
  # Check whether this section was inactive
  cmpw %r2h SNAKE_INACTIVE
  jmpe GAME_BOARD_ADD_SNAKES_FINISHED
  # Prepare for call to coordinate converter
  movd %arg1 %ret1
  call CONVERT_COORDINATES_TO_GAME_BOARD_ADDRESS
  popw %a2l # Restore the game board marker
  stow %r1l %a2l
  # Restore and increment the snake pointer
  popd %arg1
  addd %arg1 %arg1 $0x1
  jmp GAME_BOARD_ADD_SNAKES_HELPER # Tail recursive call
  GAME_BOARD_ADD_SNAKES_FINISHED:
    addd %SP %SP $0x3 # Clean up stack from pushing arg1 and a2l earlier
    ret
# End GAME_BOARD_ADD_SNAKES_HELPER


# Game Board Add Food
# Put the food on the in-memory game board
# Uses the same struct as a snake, ignoring everything except the row and column
# Single-word:
# {active,  U D L R,  column, row}
#  [15]  ,  [14:13],  [12:6], [5:0]
# Arguments:
# None
# Return:
# void
GAME_BOARD_ADD_FOOD:
  movd %arg1 FOOD_ADDRESS
  loadw %a1l %a1l # Don't need the address any more, just load into the same register
  call SNAKE_SEGMENT_UNPACK
  movd %arg1 %ret1
  call CONVERT_COORDINATES_TO_GAME_BOARD_ADDRESS
  stow %r1l GAME_BOARD_FOOD
  ret


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
    cmpw %r1h GAME_BOARD_PLAYER_1_SNAKE
    jmpe COPY_GAME_BOARD_TO_VGA_PREPARE_BLUE_SNAKE
    cmpw %r1h GAME_BOARD_PLAYER_2_SNAKE
    jmpe COPY_GAME_BOARD_TO_VGA_PREPARE_ORANGE_SNAKE
    cmpw %r1h GAME_BOARD_WALL
    jmpe COPY_GAME_BOARD_TO_VGA_PREPARE_WALL

    COPY_GAME_BOARD_TO_VGA_PREPARE_EMPTY:
      movw %r2l EMPTY_COLOUR
      movw %r1l EMPTY_ASCII_VALUE
      jmp COPY_GAME_BOARD_TO_VGA_DOIT

    COPY_GAME_BOARD_TO_VGA_PREPARE_FOOD:
      movw %r2l FOOD_COLOUR
      movw %r1l EMPTY_ASCII_VALUE
      jmp COPY_GAME_BOARD_TO_VGA_DOIT

    COPY_GAME_BOARD_TO_VGA_PREPARE_BLUE_SNAKE:
      movw %r2l BLUE_SNAKE_COLOUR
      movw %r1l EMPTY_ASCII_VALUE
      jmp COPY_GAME_BOARD_TO_VGA_DOIT

    COPY_GAME_BOARD_TO_VGA_PREPARE_ORANGE_SNAKE:
      movw %r2l ORANGE_SNAKE_COLOUR
      movw %r1l EMPTY_ASCII_VALUE
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
# End COPY_GAME_BOARD_TO_VGArds
  #     Go to the player snake addresses and change the head direction 

  call CONTROLLER_READ
  # Decode controller 1
  movd %arg1 %ret1


#### Main Game Loop
MAIN_GAME_LOOP:
  ## game tick
  movd %arg1 GAME_TICK_VALUE
  GAME_TICK_DELAY:
    subd %arg1 %arg1 $0x1
    cmpd %arg1 $0x0
    jmpb GAME_TICK_DELAY # 0x0 <? %arg1
  # end GAME_TICK_DELAY

  ### Player control
  #   read the controllers
  #     query the controller module
  #     return the two players' directions in the two return regs
  #   update head direction
  #     If there's no input, the head direction should not be changed
  #     Prevent the player from going backwa
  pushd %ret2 # Save controller 2's data
  call CONVERT_CONTROLLER_TO_DIRECTION
  popd %arg1 # Restore controller 2's data, prepare for call
  pushw %r1l # Save controller 1's decoded output
  call CONVERT_CONTROLLER_TO_DIRECTION
  pushw %r1l # Save controller 2's decoded output

  ## stack : (top) [r1l : c2_decoded] [r1l : c1_decoded]
  # todo: optimize the push then pop above and below this comment

  # Check the next move for collisions
  # Snek 2
  movd %arg1 SNAKE_2_BASE_ADDR
  popw %a2l # Prepare controller 2 direction for call
  call CHECK_COLLISIONS
  # Snek 1
  movd %arg1 SNAKE_2_BASE_ADDR
  popw %a2l # Prepare controller 1 direction for call
  call CHECK_COLLISIONS


  ## todo/thought-train pit stop: the collision checker should be the one to change the game state

  # We now have controller 1's output in %a2l and controller 2's output in %r1l
  # Note that those 'directions' may be 0xFFFF if the controller didn't have any pressed 
  # buttons! This needs to be handled.
  # Note that the SHUFFLE_SNAKE function will take a single snake segment
  # and advance all segments following that one. If the head is handled
  # specially, be sure to give the second segment to SHUFFLE_SNAKE

  # Move the snakes
  # TODO: Put the new snake head direction as %a2l for these calls
  movd %arg1 SNAKE_1_BASE_ADDR
  call SHUFFLE_SNAKE
  movd %arg1 SNAKE_2_BASE_ADDR
  movw %a2l SNAKE_DIRECTION_UP
  call SHUFFLE_SNAKE
  
  call UPDATE_GAME_BOARD

  # Update the monitor
  call COPY_GAME_BOARD_TO_VGA

  jmp MAIN_GAME_LOOP
# end MAIN_GAME_LOOP


# Convert Controller To Direction
# Extract the game direction from a controller
# Returns -1 (0xFFFF) if none of the directions were pushed
# Arguments:
# %arg1 - Controller Bitfield
# Return:
# %r1l - Controller's direction input
CONVERT_CONTROLLER_TO_DIRECTION:
  # %a1l has the lower bits from the controller
  # Using the bitmasks (see header comment), we can extract the direction
  andw %a1h %a1l $0x10 # Mask Up
    cmpw %a1h $0x10
    jmpe CONVERT_CONTROLLER_TO_DIRECTION_UP
  andw %a1h %a1l $0x20 # Mask Down
    cmpw %a1h $0x20
    jmpe CONVERT_CONTROLLER_TO_DIRECTION_DOWN
  andw %a1h %a1l $0x40 # Mask Left
    cmpw %a1h $0x40
    jmpe CONVERT_CONTROLLER_TO_DIRECTION_LEFT
  andw %a1h %a1l $0x80 # Mask Right
    cmpw %a1h $0x80
    jmpe CONVERT_CONTROLLER_TO_DIRECTION_RIGHT
  # If none of those, return a fail code
  movw %r1l $0xFFFF
  ret

  CONVERT_CONTROLLER_TO_DIRECTION_UP:
    movw %r1l SNAKE_DIRECTION_UP
    ret
  CONVERT_CONTROLLER_TO_DIRECTION_DOWN:
    movw %r1l SNAKE_DIRECTION_DOWN
    ret
  CONVERT_CONTROLLER_TO_DIRECTION_LEFT:
    movw %r1l SNAKE_DIRECTION_LEFT
    ret
  CONVERT_CONTROLLER_TO_DIRECTION_RIGHT:
    movw %r1l SNAKE_DIRECTION_RIGHT
    ret
# End CONVERT_CONTROLLER_TO_DIRECTION


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


# Check Collisions
# Check the passed snake head's next move against the game board and snakes for
# collisions. update the game state to reflect those collisions. Possible collisions
# include: no collision, food, wall, or snake
# Arguments:
# %arg1 - The base address of the snake
# %a2l  - The chosen direction
# Return:
# void
CHECK_COLLISIONS:
  # grab the coordinates of the snake head
  pushd %arg1 # Save the snake head for later

  # increment by one in the chosen direction

  # use that coordinate to grab data from the game board
  # todo: set up arguments
  call CONVERT_COORDINATES_TO_GAME_BOARD_ENTITY
  #entity type at r1l

  # Depending on what's on the game board at that coordinate:
  # If there's nothing there, do nothing and return
  cmpw %r1l GAME_BOARD_EMPTY
  jmpe COLLISION_EMPTY

  # If there's a wall or another snake, kill the snake
  cmpw %r1l GAME_BOARD_WALL
  jmpe COLLISION_WALL_OR_SNAKE

  cmpw %r1l GAME_BOARD_PLAYER_1_SNAKE
  jmpe COLLISION_WALL_OR_SNAKE

  cmpw %r1l GAME_BOARD_PLAYER_2_SNAKE
  jmpe COLLISION_WALL_OR_SNAKE

  # If there's food, grow the snake by placing a segment at the tail
  #cmpw %r1l GAME_BOARD_FOOD # Unnecessary comparison, since this is the only thing left
  jmp COLLISION_FOOD
  # I should check that the direction for this last piece doesn't matter

  COLLISION_EMPTY:
    ret
  COLLISION_WALL_OR_SNAKE:
    popd %arg1 # restore the snake head address
    pushd %arg1 # save it for after the call to nullify
    NULLIFY_SNAKE
    # Find out which snake to respawn
    popd %arg1
    cmp %arg1 SNAKE_2_BASE_ADDR
    jmpe RESPAWN_SNAKE_2

    RESPAWN_SNAKE_1:
      call SPAWN_SNAKE_1
      ret
    RESPAWN_SNAKE_2:
      call SPAWN_SNAKE_2
      ret
  COLLISION_FOOD:
    #travel to the tail of the snake

    ret 
#end CHECK_COLLISIONS

# Shuffle Snakes
# Move the passed snake forward one position
# 'Forward', in this case, is calculated by moving each piece one step in the direction
# it is facing, then setting its direction to be the direction of the piece in front of it
# Arguments:
# %arg1 - The base address of the snake to move
# %a2l  - The direction of the previous segment
# Return:
# void
SHUFFLE_SNAKE:
  pushd %arg1 # Save the current address into the snake
  pushw %a2l  # Save the leading segment's direction
  loadw %a1l %a1l # Load the next segment
  call SNAKE_SEGMENT_UNPACK
  # Check whether this section was inactive
  cmpw %r2h SNAKE_INACTIVE
  jmpe SHUFFLE_SNAKE_FINISHED
  # Decide which direction the snake was going
  cmpw %r2l SNAKE_DIRECTION_LEFT
    jmpe SHUFFLE_SNAKE_LEFT
  cmpw %r2l SNAKE_DIRECTION_RIGHT
    jmpe SHUFFLE_SNAKE_RIGHT
  cmpw %r2l SNAKE_DIRECTION_UP
    jmpe SHUFFLE_SNAKE_UP
  cmpw %r2l SNAKE_DIRECTION_DOWN
    jmpe SHUFFLE_SNAKE_DOWN

  SHUFFLE_SNAKE_LEFT:
    # Decrease the column coordinate by 1
    subw %r1h %r1h $0x1
    jmp SHUFFLE_SNAKE_WRITEBACK
  SHUFFLE_SNAKE_RIGHT:
    # Increase the column coordinate by 1
    addw %r1h %r1h $0x1
    jmp SHUFFLE_SNAKE_WRITEBACK
  SHUFFLE_SNAKE_UP:
    # Decrease the row coordinate by 1
    subw %r1l %r1l $0x1
    jmp SHUFFLE_SNAKE_WRITEBACK
  SHUFFLE_SNAKE_DOWN:
    # Increase the row coordinate by 1
    addw %r1l %r1l $0x1
    jmp SHUFFLE_SNAKE_WRITEBACK

  SHUFFLE_SNAKE_WRITEBACK:
    # Restore the last segment's direction
    popw %a3l
    # Save this segment's direction. We will need to pass it to the next call
    pushw %r2l 
    # Update this segment's direction
    movw %r2l %a3l

    # Pack the segment
    movd %arg1 %ret1
    movd %arg2 %ret2
    call SNAKE_SEGMENT_PACK
    popw %a2l  # Restore this segment's direction
    popd %arg1 # Restore this segment's address
    stow %a1l %r1l # Write this segment
    addd %arg1 %arg1 $0x1 # Move to the next segment
    jmp SHUFFLE_SNAKE # Tail recursive call

  SHUFFLE_SNAKE_FINISHED:
    addd %SP %SP $0x3 # Clean up stack from pushing arg1 and %a2l earlier
    ret
# End SHUFFLE_SNAKE


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
