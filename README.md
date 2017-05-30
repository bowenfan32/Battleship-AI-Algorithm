# Battleship-AI-Algorithm

Created three different types of AI players:
1. Random guessing player: the AI will guess randomly across the board.
2. Greedy guessing player: the AI will first guess in a checkerboard type of pattern, upon hitting a ship, it will guess neighbouring cell and sink one ship before moving to the next.
3. Monte Carlo player: The AI will do a probabilistic sampling to find out where the ship is most likely to be, upon every hit/miss, it will re-sample the board based on information already known and tires to hit cells with highest probability.
