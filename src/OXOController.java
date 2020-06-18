class OXOController
{
    private int matchingCells = 0;
    private int totalPlayers;
    private int playerNumber = 0;
    private int currRow;
    private int currCol;
    private int cellsFilled = 0;
    private int totalCells;
    private OXOModel model;
    private OXOPlayer currPlayer;
    private boolean winFlag = false; // to ensure no more input accepted

    public OXOController(OXOModel model)
    {
        this.model = model;
        totalPlayers = model.getNumberOfPlayers();
        totalPlayers--; // to start at player 0
        currPlayer = model.getPlayerByNumber(playerNumber);
        model.setCurrentPlayer(model.getPlayerByNumber(0));
        totalCells = model.getNumberOfRows() * model.getNumberOfColumns();
    }


    public void handleIncomingCommand(String command) throws InvalidCellIdentifierException, CellAlreadyTakenException, CellDoesNotExistException
    {
        if((!winFlag) && (!model.isGameDrawn())) // to ignore input after game won / is a stalemate
        {
            command = command.toLowerCase();
            if (!(checkIfInvalid(command))) {
                throw new InvalidCellIdentifierException("entered", command);
            }
            findCurrentCell(command);
            if (!(checkIfTaken(model))) {
                throw new CellAlreadyTakenException(currRow, currCol);
            }
            if (!(checkIfCellExists(model))) {
                throw new CellDoesNotExistException(currRow, currCol);
            }
           beginGamePlay(model);
        }
    }

    private void beginGamePlay(OXOModel model)
    {
        model.setCellOwner(currRow, currCol, currPlayer);
        cellsFilled++;
        if(cellsFilled == totalCells) {
            model.setGameDrawn();
            return;
        }
        if (checkIfWin(model)) {
            gameWin(model);
            return;
        }
        togglePlayer(model);
        model.setCurrentPlayer(model.getPlayerByNumber(playerNumber));
    }

    private void gameWin(OXOModel model)
    {
        model.setWinner(currPlayer);
        winFlag = true;
    }

    private void togglePlayer(OXOModel model)
    {
        // back to player 0 after each has gone
        if(playerNumber == totalPlayers) {
            playerNumber = 0;
        }
        else {
            playerNumber++;
        }
        currPlayer = model.getPlayerByNumber(playerNumber);
    }

    private boolean checkIfWin(OXOModel model)
    {
        return checkRowWin(model) || checkColumnWin(model) || checkRightDiagonalWin(model) || checkLeftDiagonalWin(model);
    }

    private boolean checkRowWin(OXOModel model)
    {
        for(int i = 0; i < model.getNumberOfRows(); ++i) {
            checkIfMatch(model, currRow, i);
            if (model.getWinThreshold() == matchingCells) {
                matchingCells = 0;
                return true;
            }
        }
        matchingCells = 0;
        return false;
    }

    private boolean checkColumnWin(OXOModel model)
    {
        for(int i = 0; i < model.getNumberOfColumns(); i++) {
            checkIfMatch(model, i, currCol);
            if (model.getWinThreshold() == matchingCells) {
                matchingCells = 0;
                return true;
            }
        }
        matchingCells = 0;
        return false;
    }

    private boolean checkRightDiagonalWin(OXOModel model)
    {
        int tempRow, tempCol;
        // up to left - using y, x coordinates w/ tempRow
        for(tempRow = currRow, tempCol = currCol ; tempRow >= 0 && tempCol >= 0; --tempRow, --tempCol) {
            checkIfMatch(model, tempRow, tempCol);
            if(model.getWinThreshold() == matchingCells) {
                return true;
            }
        }
        matchingCells = 0;
        // down to the Right
        for(tempRow = currRow, tempCol = currCol; tempRow <= model.getNumberOfRows() && tempCol <= model.getNumberOfColumns(); ++tempRow, ++tempCol) {
            checkIfMatch(model, tempRow, tempCol);
            if(model.getWinThreshold() == matchingCells){
                return true;
            }
        }
        matchingCells = 0;
        return false;
    }

    private boolean checkLeftDiagonalWin(OXOModel model){
        int tempRow, tempCol;
        // up to the right - using y, x coordinates w/ tempRow
        for(tempRow = currRow, tempCol = currCol ; tempRow <= model.getNumberOfRows() && tempCol >= 0; ++tempRow, --tempCol){
            checkIfMatch(model, tempRow, tempCol);
            if(model.getWinThreshold() == matchingCells) {
                return true;
            }
        }
        matchingCells = 0;
        // down to the left
        for(tempRow = currRow, tempCol = currCol; tempRow >= 0 && tempCol <= model.getNumberOfColumns(); --tempRow, ++tempCol){
            checkIfMatch(model, tempRow, tempCol);
            if(model.getWinThreshold() == matchingCells) {
                return true;
            }
        }
        matchingCells = 0;
        return false;
    }

    private void checkIfMatch(OXOModel model, int tempRow, int tempCol)
    {
        OXOPlayer cellOwner = model.getCellOwner(tempRow, tempCol);
        if(cellOwner == currPlayer){
            ++matchingCells;
        }
        else{
            matchingCells = 0;
        }
    }

    private boolean checkIfTaken(OXOModel model)
    {
        return model.getCellOwner(currRow, currCol) == null;
    }

    private boolean checkIfInvalid(String command)
    {
        // only accepts up to '9' for cell - 2 char input
        if (command.length() != 2){
            return false;
        }
        if(!(Character.isLetter(command.charAt(0))))
        {
            return false;
        }
        else return Character.isDigit(command.charAt(1));
    }

    private boolean checkIfCellExists(OXOModel model)
    {
        return currRow < model.getNumberOfRows() && currRow >= 0 && currCol < model.getNumberOfColumns() && currCol >= 0;
    }

    private void findCurrentCell(String command)
    {
        // get numeric value
        currRow = command.charAt(0) - 'a';
        currCol = command.charAt(1) - '1';
    }
}
