package com.example.adam.sudoku;

/**
 * Created by Adam on 19/02/2018.
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Arrays;

public class Game extends AppCompatActivity {
    SudokuBoard sudokuBoard;
    TextView[][] cells = new TextView[9][9];
    View currView = null;
    String currCell = "";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void startGame(View view)
    {
        sudokuBoard = initSudokuBoard();
        sudokuBoard.createSudoku();
        sudokuBoard.makeHoles();
        sudokuBoard.printGame();
        sudokuBoard.calculateSmall();

        // Just for Test TODO remove later
        System.out.println(Arrays.toString(sudokuBoard.gameBoard[0][0].getSmallNumbers()));

        setContentView(R.layout.game);

        for(int i = 0; i < 9; i++)
        {
            for(int j = 0; j < 9; j++)
            {
                String textID = "Cell" + i + "-" + j;
                int resID = getResources().getIdentifier(textID, "id", getPackageName());
                cells[i][j] = ((TextView)findViewById(resID));
                cells[i][j].setText("" + sudokuBoard.gameBoard[i][j].getNumber());
                if(sudokuBoard.gameBoard[i][j].getNumber() != 0) cells[i][j].setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void openSettings(View view) {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

    private void refreshCells(int x, int y)
    {
        cells[x][y].setText("" + sudokuBoard.gameBoard[x][y].getNumber());
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void btnCell(View view)
    {
        String id = view.getResources().getResourceName(view.getId());
        id = id.substring(id.length() - 3, id.length());
        if(sudokuBoard.gameBoard[Integer.parseInt(id.substring(0,1))][Integer.parseInt(id.substring(2,3))].getIsEditable())
        {
            currCell = id;
            setSelected(view);
            //refreshCells(Integer.parseInt(id.substring(0,1)),Integer.parseInt(id.substring(2,3)));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setSelected(View view)
    {
        if(currView != null) currView.setBackgroundTintList(null);
        if(currView == view)
        {
            currView.setBackgroundTintList(null);
            currView = null;
            currCell = "";
        }
        else
        {
            view.setBackgroundTintList(ColorStateList.valueOf(Color.BLUE));
            currView = view;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void addNum(View view)
    {
        if(currCell != "")
        {
            String num = view.getResources().getResourceName(view.getId());
            sudokuBoard.gameBoard[Integer.parseInt(currCell.substring(0,1))][Integer.parseInt(currCell.substring(2,3))].setNumber(Integer.parseInt(num.substring(num.length() - 1)));
            refreshCells(Integer.parseInt(currCell.substring(0,1)),Integer.parseInt(currCell.substring(2,3)));
            currCell = "";
            currView.setBackgroundTintList(null);
            currView = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
    }

    protected SudokuBoard initSudokuBoard() {
        String difficulty = getDifficulty();

        switch(difficulty) {
            case "easy":
                return new EasyGame();
            case "medium":
                return new MediumGame();
            case "hard":
                return new HardGame();
            default:
                return new MediumGame();
        }
    }

    protected String getDifficulty() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getString("pref_difficulty", "");

    }
}
