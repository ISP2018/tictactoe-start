## Tic-Tac-Toe

### Description

A tic-tac-toe game written using JavaFX.

| Class          | Responsibility              |
|----------------|-----------------------------|
| Main           | Start the app, show the view. |
| GameUI.fxml    | FXML file for game board and buttons. |
| GameController | Handle user input, update game. |
| TicTacToeGame  | "Model" in MVC. Logic for tic-tac-toe. Also updates board. |
| Board          | View of the game board, using JavaFX components. |
| Player         | Enum of players: X, O, and NONE. |
| Piece          | Game object representing a player on a square ("X", "O", or NONE)|

### Design Notes

The fxml file doesn't contain the game board.
It contains a Pane with fx:id `centerPane`. The controller
dynamically adds the board (a GridPane object) to the pane.
This is so that (a) we can change the size of the board,
(b) the controller and model (TicTacToeGame) have a reference
to the board so they can add and remove pieces.

## Exercises (Revised 17 August)

**Revised 17 Aug 2018** I reordered and clarified the assignment.  The ISP2018 class does **not** have to follow these instructions, but some parts may be helpful.

Do the following exercises in the order shown.
When done, push all your work including all branches to Github.

### 1. Create a Development (`dev`) Branch

We don't want to work directly on "master", so create a `dev` branch
and do your work there.


### 1. Code Review

Review the code in the TicTacToeGame class.  Since code review takes time, just do this one class.

Try to find 3 issues.

1. Review the entire code. Look the following kinds of defects:
   * Missing or incomplete class Javadoc. (Ignore method Javadoc.)
   * Magic Numbers: using numbers for important values instead of a variable or named constant
   * Repetitive code or logic: is same code or logic repeated? 
   * Logic errors: the most important!  A logic error is code that performs incorrect logic.  These can be hard to find when reviewing someone else's code.
2. For each defect, create an issue in your Github repo for the assignment, and give it a name.
   * If the issue is connected to specific lines of code, include a reference to the line numbers (demo how to in class).  You can also create issues by clicking to the left of a line of code on Github.

### 2. Fix and Close the Issues Using Commit Messages

1. Working on the `dev` branch, fix the issues you found.
2. Test the code.  
   * For issues like Javadoc and bad coding, you'll need to review the code to verify its correct.  For Javadoc, you can look at the Javadoc inside your IDE (does it look good?).
   * For other issues, test by running and playing the game. You should test 3 cases: X wins, O wins, Draw.  Be creative.
3. When fixed, commit the code and close issues using [keywords in commit messages](https://help.github.com/articles/closing-issues-using-keywords/).    
   For example: `git commit -m "This closes #2"`. When you push code to Github it will close issue 2.  See [list of keywords](https://help.github.com/articles/closing-issues-using-keywords/) that Github recognizes.

### 3. Merge `dev` into `master` and push updates

1. Once the code is working, merge changes from `dev` into `master` on your local repo.
2. Push the updated `master` to Github.
3. On the Github web interface, verify that all issues are now closed.

### 4. Create a Release Tag
 
1. Add an annotated tag named "VER_1.0" for this release, on the `master` branch of your local repository:   
   ```git tag -a -m "Release version 1.0 of Tic-tac-toe" VER_1.0```
2. Push the tag to Github using `git push --tags`.  
3. Go to Github's web interface and verify that the tag is there.

### 5. Add a New Feature: 4x4 Tic-Tac-Toe

The World TicTacToe Association (WTA) wants to you modify the game to use a 4x4 board.

1. Add an issue for this.  Give the issue a label of "enhancement". Labels are shown on the right of the New Issue screen.
2. Start work on the `dev` branch.


### 6. Flash News!  A Serious Bug.

1. There is a serious **bug** in Version 1.0.  You need to fix it immediately.  Bug described in class.
2. Commit your work on the local `dev` branch.
3. Switch back to `master` and checkout the VER_1.0 revision.  This is probably still the HEAD on master.
4. Verify that you can reproduce the bug.
5. Add an issue on Github with label **bug** (the red label):
   > Title: Player can still move after game is over.    
   > Description:    
   > A player can still make a move after the game has been won.
   > In some cases, the losing player can become winner by making a move after game is over.
5. Create a new branch: `fix_gameover_bug`. Switch to this branch to fix the bug.
6. Fix the bug.  See if you can find and fix it yourself.  For the Java-challenged, some help is provided below.
7. Test Your Solution.  Moves should not be allowed after the game is won.

#### Tracking Down the Problem

First, what happens when a player clicks on a square?  We need to look in the controller class (GameController) for that.  The `initialize()` method adds an `OnMouseClick` event handler to each square. The method is `handleCellClicked()`.

Next, what does `handleCellClicked()` do?  The important part of the code is:
```java
    if (game.canMoveTo(player, col, row)) {
        game.moveTo(new Piece(player, size), col, row);
        // The game will add piece to the board
    }
    updateGameStatus();
```
So, if we fix the logic in `game.canMoveTo()` so that it returns false when the game is over, then no one will be able to make a move!  This makes sense: it is the TicTacToeGame's responsibility to decide when the game is over.

Examime the code for `TicTacToeGame`.  In the `canMoveTo()` method, check if the game is over (the 3rd "if" statement is added):
```java
public boolean canMoveTo(Player player, int col, int row) {
	if (row<0 || row>pieces.length) return false;
	if (col<0 || col>pieces[row].length) return false;
       if ( isGameOver() ) return false;   // NEW: don't allow move 
	return pieces[row][col] == null || pieces[row][col] == Piece.NONE;
}
```

Compile and run the code.  Does it fix the problem?

Answer: No.

There is another bug.  When a player "wins" the code does not always change the value of `gameOver`.  An easy way to fix this is in `moveTo`. After each move, if there is a winner then change the gameOver property.
```java
public void moveTo(Piece piece, int col, int row) {
    ...
    ...
//TODO Combine these 2 if statements into a single "if"
    /** after each move check if board is full */
	if (boardIsFull()) gameOver.set(true);
    /** always check if someone has won */
    if (winner() != Player.NONE) gameOver.set(true);
}
```

### 7. Close the Bug Issue. Merge fix into Master and Tag It.

1. Commit your code.  In the commit message add a phrase like "Fixes #4" (the issue number).
2. Switch back to `master` and merge the `fix_gameover_bug` branch into master.
3. Add an annotated tag such as `VER_1.0.1`. The tag message should mention that it includes code to fix issue #4.
4. Push everything to Github, including the tag and `fix_gameover_bug` branch.

### 8. Back to Work on 4x4 Tic-Tac-Toe

1. Switch back to the `dev` branch.
2. Merge in the bug fix code from `master`.
3. Implement 4x4 tic-tac-toe.
4. Test it, Review it.  We don't want another embarassing bug.
5. When your code works, do 3 things:
   a. create a runnable JAR file of the game in directory `dist/` inside the project top-level dir (**not** inside the `src/` dir).
   b. Commit everything including the JAR file.
   c. Merge commits from `dev` into `master`.
6. On `master` add the annotated tag `VER_2.0`.
7. Commit everything and push to Github.

### What to Submit

On Github you should have:

1. Issues for all the bugs and defects (all closed).
2. Issues for the 4x4 "enhancement".
3. These branches: `master`, `dev`, `fix_gameover_bug`.
4. Tags: `VER_1.0`, `VER_1.0.1`, `VER_2.0`.
5. Up-to-date source code on `master`.
6. A runnable Jar file on `master` in `/dist`.


