import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.Random;
import java.awt.Color;

public class GamePanel extends JPanel implements ActionListener
{
    
    // instance variables - replace the example below with your own
    private PieceProxy _piece;
    private Timer _timer;
    private Random _generator;
    
    private KeyUpListener _upKey;
    private KeyDownListener _downKey;
    private KeyLeftListener _leftKey;
    private KeyRightListener _rightKey;
    private KeyPListener _pauseKey;
    private KeySpaceListener _spaceKey;
    private SmartRectangle[][] _board = new SmartRectangle[TetrisConstants.BOARD_HEIGHT][TetrisConstants.BOARD_WIDTH];
   //    private SmartRectangle[][] _board = new SmartRectangle[1024][1024];    
    private boolean _gameOver = false;
    
    /**
     * Constructor for objects of class GamePanel
     */
     public GamePanel()
    {
        // initialise instance variables
        this.setBackground(Color.BLACK);
        this.setSize(new Dimension(TetrisConstants.BLOCK_SIZE*(TetrisConstants.BOARD_WIDTH), TetrisConstants.BLOCK_SIZE*(TetrisConstants.BOARD_HEIGHT)+15));
        this.setPreferredSize(new Dimension(TetrisConstants.BLOCK_SIZE*(TetrisConstants.BOARD_WIDTH), TetrisConstants.BLOCK_SIZE*(TetrisConstants.BOARD_HEIGHT)+15));

        _upKey = new KeyUpListener(this);
        _downKey = new KeyDownListener(this);
        _leftKey = new KeyLeftListener(this);
        _rightKey = new KeyRightListener(this);
        _pauseKey = new KeyPListener(this);
        _spaceKey = new KeySpaceListener(this);

        _generator = new Random();
        
        _piece = new PieceProxy();
        _piece.setPiece(tetriminoFactory());

        _timer = new Timer(500, this);
        _timer.start();
              
            
        }

    
    public Tetrimino tetriminoFactory()
    /** 
     * This method implements the factory method design pattern to build new tetriminos during Tetris game play.
     */
    {
        Tetrimino newPiece;
        int randomNumber;
        
        int x = (TetrisConstants.BOARD_WIDTH/2) * TetrisConstants.BLOCK_SIZE;
        int y = 0;
        randomNumber = (int) (Math.floor(Math.random()*7)+1);
      //randomNumber = 6;
        switch(randomNumber) {
            case 1: newPiece = new Z(x,y, this);     break;
            case 2: newPiece = new S(x,y, this);     break;
            case 3: newPiece = new L(x,y, this);     break;
            case 4: newPiece = new J(x,y, this);     break;
            case 5: newPiece = new O(x,y, this);     break;
            case 6: newPiece = new I(x,y, this);     break;
            default: newPiece = new T(x,y,this);     break;
        }
        return newPiece;
    }
    
    public void paintComponent (java.awt.Graphics aBrush) 
    {
        super.paintComponent(aBrush);
        java.awt.Graphics2D betterBrush = (java.awt.Graphics2D)aBrush;
        
        
        for (int i = 0; i < (TetrisConstants.BOARD_HEIGHT); i++) {
            for (int j = 0; j < (TetrisConstants.BOARD_WIDTH); j++) {
                if (_board[i][j] != null) {
                    _board[i][j].fill(betterBrush);
                    _board[i][j].draw(betterBrush);
                }
            }
        }
        
        _piece.fill(betterBrush);
        _piece.draw(betterBrush);
    }
    /**
     * This method takes two integers representing the column and row of a cell on the game board a component rectangle into which a
     * tetrimino wishes to move. This can be prevented by either the cell being off of the game board (not a valid cell) or by the
     * cell being occupied by another SmartRectangle.
     * 
     * @param c The column of the cell in question on the game board.
     * @param r The row of the cell in question on the game board.
     * @return boolean This function returns whether the component rectangle can be moved into this cell.
     */
    public boolean canMove(int c, int r)
    {   
        return (isValid(c,r) && isFree(c,r));
    }
    
    /**
     * This method takes two integers representing the column and row of a cell on the game board a component rectangle into which a
     * tetrimino wishes to move. This method returns a boolean indicating whether the cell on the game board is empty.
     * 
     * @param c The column of the cell in question on the game board.
     * @param r The row of the cell in question on the game board.
     * @return boolean This function returns whether the cell on the game board is free.
     */    
    private boolean isFree(int c, int r)
    {   
    
            return _board[r][c] == null;

    }
    
    /**
     * This method takes two integers representing the column and row of a cell on the game board a component rectangle into which a
     * tetrimino wishes to move. This function checks to see if the cell at (c, r) is a valid location on the game board.
     * 
     * @param c The column of the cell in question on the game board.
     * @param r The row of the cell in question on the game board.
     * @return boolean This function returns whether the location (c, r) is within the bounds of the game board.
     */
    private boolean isValid(int c, int r)    {
      //  if(r<=0 || r>=TetrisConstants.BLOCK_SIZE*TetrisConstants.BOARD_HEIGHT || _board[r][c] == null) 
        if(r<0 || r>=TetrisConstants.BOARD_HEIGHT) {
              return false;
        }
        else if(c<0|| c>=TetrisConstants.BOARD_WIDTH) {
            
            return false;
        }
        else {
            return true;
        }
    }
     /**
     * This method takes two integers representing the column and row of a cell on the game board a component rectangle into which a
     * tetrimino wishes to move. This can be prevented by either the cell being off of the game board (not a valid cell) or by the
     * cell being occupied by another SmartRectangle.
     * 
     * @param r The SmartRectangle to add to the game board.
     * @return Nothing
     */   
    public void addToBoard(SmartRectangle r) {
        if (canMove((int) (r.getX() / TetrisConstants.BLOCK_SIZE),((int) (r.getY() / TetrisConstants.BLOCK_SIZE)))) {
        _board[(int) (r.getY() / TetrisConstants.BLOCK_SIZE)][(int) (r.getX() / TetrisConstants.BLOCK_SIZE)] = r;
    }
    }
    /**
     * This method takes one integer representing the row of cells on the game board to move down on the screen after a full 
     * row of squares has been removed.
     * 
     * @param row The row in question on the game board.
     * @return Nothing
     */
    private void moveBlocksDown(int row) {
        //starting at a row you know is null from checkRows
        //nothing below the row you start at needs to be modified
        //every block in a row above you needs to be moved down one array row
        int start = row - 1;
        int col = 0;

        while (start >= 0) {
            while (col < TetrisConstants.BOARD_WIDTH) {
                if (_board[start][col] != null) {
                    _board[start][col].y += TetrisConstants.BLOCK_SIZE;                
                }
                _board[start+1][col] = _board[start][col];
                col++;              
            }
            start--;
            col = 0;
    }
}
    
    /**
     * This method checks each row of the game board to see if it is full of rectangles and should be removed. It calls
     * moveBlocksDown to adjust the game board after the removal of a row.
     * 
     * @return Nothing
     */
    private void checkRows(){
        //start at the bottom of the _board array and check if a row is full 
        //if a row is full, set that row in the array equal to 0
        //call the moveBlocksDown method and give it the row you found
        //that used to be full but its now null
        //make sure it loops a.k.a. does not find one full row and stop+
        int count=0;
        for (int i=0;i<TetrisConstants.BOARD_HEIGHT;i++){
            for (int j=0;j<TetrisConstants.BOARD_WIDTH;j++){
                if (_board[i][j]!=null){
                    count++;
                }
            }
            if (count == TetrisConstants.BOARD_WIDTH){
                for (int k=0;k<TetrisConstants.BOARD_WIDTH;k++){
                    _board[i][k] = null;
                }
                moveBlocksDown(i);
            }
            count=0;
        }
    }
    /**
     * This method checks to see if the game has ended.
     * 
     * @return boolean This function returns whether the game is over or not.
     */
    private boolean checkEndGame()
    {
        for(int i=0; i<(TetrisConstants.BOARD_WIDTH); i++) {
            if(_board[0][i] !=  null) {
                _gameOver = true;
                return true;
            }
        }
        
        return false;
    }
    public void actionPerformed(ActionEvent e)
    {
        
     if (!(checkEndGame())) {
         
            if (_piece.moveDown()) {
                repaint();
            }
        
            else {
                checkRows();
                if (!(checkEndGame())) {                   
                    _piece = new PieceProxy();
                    _piece.setPiece(tetriminoFactory()); 
                }
        repaint();
        
    }
}
    
    else {
            _timer.stop();
            }
            
            repaint();
        
        
    }
    
    
    /*{ //  System.out.println (TetrisConstants.BLOCK_SIZE*(TetrisConstants.BOARD_HEIGHT));
      //  System.out.println (TetrisConstants.BLOCK_SIZE*(TetrisConstants.BOARD_WIDTH)); 
     //   System.out.println (_piece.getX()); 
     //   System.out.println (_piece.getY());
   //  boolean paint = true;
      if (!(checkEndGame())) {
      
        if (canMove( (int) _piece.getP()._block1.getX()/ TetrisConstants.BLOCK_SIZE, (int) (_piece.getP()._block1.getY()/ TetrisConstants.BLOCK_SIZE) + 1) && 
                   canMove( (int) _piece.getP()._block2.getX()/ TetrisConstants.BLOCK_SIZE, (int) (_piece.getP()._block2.getY()/ TetrisConstants.BLOCK_SIZE) + 1) &&
                   canMove( (int) _piece.getP()._block3.getX()/ TetrisConstants.BLOCK_SIZE, (int) (_piece.getP()._block3.getY()/ TetrisConstants.BLOCK_SIZE) + 1) &&
                   canMove( (int) _piece.getP()._block4.getX()/ TetrisConstants.BLOCK_SIZE, (int) (_piece.getP()._block4.getY()/ TetrisConstants.BLOCK_SIZE) + 1))
                {
                _piece.moveDown();  
                
               
                
            }   
        else {
            addToBoard(_piece.getP()._block1);
            addToBoard(_piece.getP()._block2);
            addToBoard(_piece.getP()._block3);
            addToBoard(_piece.getP()._block4);
            checkRows();
           if (!(checkEndGame())) {
            _piece = new PieceProxy();
            _piece.setPiece(tetriminoFactory()); 
        }
            
              if (!(canMove( (int) _piece.getP()._block1.getX()/ TetrisConstants.BLOCK_SIZE, (int) (_piece.getP()._block1.getY()/ TetrisConstants.BLOCK_SIZE) + 1) && 
                   canMove( (int) _piece.getP()._block2.getX()/ TetrisConstants.BLOCK_SIZE, (int) (_piece.getP()._block2.getY()/ TetrisConstants.BLOCK_SIZE) + 1) &&
                   canMove( (int) _piece.getP()._block3.getX()/ TetrisConstants.BLOCK_SIZE, (int) (_piece.getP()._block3.getY()/ TetrisConstants.BLOCK_SIZE) + 1) &&
                   canMove( (int) _piece.getP()._block4.getX()/ TetrisConstants.BLOCK_SIZE, (int) (_piece.getP()._block4.getY()/ TetrisConstants.BLOCK_SIZE) + 1)))
                {
                   _gameOver = true;
                }
        
                
        }
        }
            else {
                _timer.stop();
            }
            repaint();
         
    }
    
    */
    
    
    
    
    
    private class KeyUpListener extends KeyInteractor 
    {
        public KeyUpListener(JPanel p)
        {
            super(p,KeyEvent.VK_UP);
        }
        
        public  void actionPerformed (ActionEvent e) { 
             if (_timer.isRunning() && !(_gameOver)) { 
            _piece.turnRight();
             repaint();
            }
            
        }
       /*     
        if (_timer.isRunning() && !(_gameOver)) { 
        _piece.turnRight();
        if (canMove( (int) _piece.getP()._block1.getX()/ TetrisConstants.BLOCK_SIZE, (int) _piece.getP()._block1.getY()/ TetrisConstants.BLOCK_SIZE) && 
               canMove( (int) _piece.getP()._block2.getX()/ TetrisConstants.BLOCK_SIZE, (int) _piece.getP()._block2.getY()/ TetrisConstants.BLOCK_SIZE) &&
               canMove( (int) _piece.getP()._block3.getX()/ TetrisConstants.BLOCK_SIZE, (int) _piece.getP()._block3.getY()/ TetrisConstants.BLOCK_SIZE) &&
               canMove( (int) _piece.getP()._block4.getX()/ TetrisConstants.BLOCK_SIZE, (int) _piece.getP()._block4.getY()/ TetrisConstants.BLOCK_SIZE)) {
            repaint();
            }
        else {
            _piece.turnLeft();
            repaint();
             }
            }
       }
       */
    }
    private class KeyDownListener extends KeyInteractor 
   {
        public KeyDownListener(JPanel p)
        {
            super(p,KeyEvent.VK_DOWN);
        }
        
        public  void actionPerformed (ActionEvent e) {
            if (_timer.isRunning() && !(_gameOver)) {  
            if (_piece.moveDown()) {
                repaint();
                
            }
            
            else {
                checkRows();
                repaint();
            }
            
        }
            
            
    }
    }
          
              
  
    
    
    /* {
        public KeyDownListener(JPanel p)
        {
            super(p,KeyEvent.VK_DOWN);
        }
        
        public  void actionPerformed (ActionEvent e) {
        if (_timer.isRunning() && !(_gameOver)) {  
            if (canMove( (int) _piece.getP()._block1.getX()/ TetrisConstants.BLOCK_SIZE, (int) (_piece.getP()._block1.getY()/ TetrisConstants.BLOCK_SIZE) + 1) && 
                   canMove( (int) _piece.getP()._block2.getX()/ TetrisConstants.BLOCK_SIZE, (int) (_piece.getP()._block2.getY()/ TetrisConstants.BLOCK_SIZE) + 1) &&
                   canMove( (int) _piece.getP()._block3.getX()/ TetrisConstants.BLOCK_SIZE, (int) (_piece.getP()._block3.getY()/ TetrisConstants.BLOCK_SIZE) + 1) &&
                   canMove( (int) _piece.getP()._block4.getX()/ TetrisConstants.BLOCK_SIZE, (int) (_piece.getP()._block4.getY()/ TetrisConstants.BLOCK_SIZE) + 1)) {
                _piece.moveDown();
                       repaint();
            }

        }
    }
  */  
     
    private class KeyLeftListener extends KeyInteractor 
    {
        public KeyLeftListener(JPanel p)
        {
            super(p,KeyEvent.VK_LEFT);
        }
        
        public  void actionPerformed (ActionEvent e) {
        if (_timer.isRunning() && !(_gameOver)) {  
            _piece.moveLeft();              
            repaint();
            
        }
            
            
         
        
    }
            
            
            
            
        /*if (_timer.isRunning() && !(_gameOver)) { 
          _piece.moveLeft();
            if (canMove( (int) _piece.getP()._block1.getX()/ TetrisConstants.BLOCK_SIZE, (int) _piece.getP()._block1.getY()/ TetrisConstants.BLOCK_SIZE) && 
                   canMove( (int) _piece.getP()._block2.getX()/ TetrisConstants.BLOCK_SIZE, (int) _piece.getP()._block2.getY()/ TetrisConstants.BLOCK_SIZE) &&
                   canMove( (int) _piece.getP()._block3.getX()/ TetrisConstants.BLOCK_SIZE, (int) _piece.getP()._block3.getY()/ TetrisConstants.BLOCK_SIZE) &&
                   canMove( (int) _piece.getP()._block4.getX()/ TetrisConstants.BLOCK_SIZE, (int) _piece.getP()._block4.getY()/ TetrisConstants.BLOCK_SIZE)) {
                       repaint();
            }
            
            else {
                _piece.moveRight();
                repaint();
            }
           
        }
    }
    
    */
    } 
    private class KeyRightListener extends KeyInteractor 
    {
        public KeyRightListener(JPanel p)
        {
            super(p,KeyEvent.VK_RIGHT);
        }
        
        public  void actionPerformed (ActionEvent e) {
            
            if (_timer.isRunning() && !(_gameOver)) { 
                _piece.moveRight();
                    repaint();
                }
            
        }
                
           /* if (_timer.isRunning() && !(_gameOver)) { 
              _piece.moveRight(); 
            if (canMove( (int) _piece.getP()._block1.getX()/ TetrisConstants.BLOCK_SIZE, (int) _piece.getP()._block1.getY()/ TetrisConstants.BLOCK_SIZE) && 
                   canMove( (int) _piece.getP()._block2.getX()/ TetrisConstants.BLOCK_SIZE, (int) _piece.getP()._block2.getY()/ TetrisConstants.BLOCK_SIZE) &&
                   canMove( (int) _piece.getP()._block3.getX()/ TetrisConstants.BLOCK_SIZE, (int) _piece.getP()._block3.getY()/ TetrisConstants.BLOCK_SIZE) &&
                   canMove( (int) _piece.getP()._block4.getX()/ TetrisConstants.BLOCK_SIZE, (int) _piece.getP()._block4.getY()/ TetrisConstants.BLOCK_SIZE)) {
                   
                       repaint();
            }
            else {
                  _piece.moveLeft();
                  repaint();
                }
            }
          
        } */
    }
    private class KeyPListener extends KeyInteractor 
    {
        public KeyPListener(JPanel p)
        {
            super(p,KeyEvent.VK_P);
        }
        
        public  void actionPerformed (ActionEvent e) {
            if(_timer.isRunning()){
                _timer.stop();
            }
            else
                _timer.start();
        }
    }
    
    private class KeySpaceListener extends KeyInteractor 
    {
        public KeySpaceListener(JPanel p)
        {
            super(p,KeyEvent.VK_SPACE);
        }
        
        public  void actionPerformed (ActionEvent e) {
            //Check if a piece can move down a space, if it can, then do so
            //and continue checking if it can move down again. Once the piece
            //can no longer move down, set this as the new position and change
            //its place in the array _boards    
            if (_timer.isRunning() && !(_gameOver)) {                           
            boolean tester = true;
            while(tester) {
            //    _piece.moveDown();
                if    (_piece.moveDown()) {
                        tester = true;
                }
                else {
                //    _piece.moveUp();                    
                    tester = false;
                }
            }
                    checkRows();
                    if (!(checkEndGame())) {
                        _piece = new PieceProxy();
                        _piece.setPiece(tetriminoFactory()); 
            
            }
                        
            repaint();
        }
    }
}

}

