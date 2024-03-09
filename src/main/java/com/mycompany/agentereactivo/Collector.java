package com.mycompany.agentereactivo;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Collector {

    private int x;
    private int y;
    private boolean hasMineral;
    private boolean playMode;
    private int x_ship;
    private int y_ship;

    private Graphic graphic;
    private int[][] board;

    public Collector(Graphic graphic, int x, int y, ArrayList<int[]> shipsPositions) {
        int random = (int) (Math.random() * (1000));
        int shipsNumber = shipsPositions.size();
        int randomShips = (int) (Math.random() * shipsNumber);

        this.x = x;
        this.y = y;

        this.hasMineral = false;
        this.playMode = true;

        this.x_ship = shipsPositions.get(randomShips)[0];
        this.y_ship = shipsPositions.get(randomShips)[1];

        System.out.println("current:" + x + ',' + y);
        System.out.println("ship:" + x_ship + ',' + y_ship);

        this.graphic = graphic;
        this.board = graphic.getBoard();

        Thread thread = new Thread(task);

        try {
            thread.sleep(random);
        } catch (InterruptedException ex) {
            Logger.getLogger(Collector.class.getName()).log(Level.SEVERE, null, ex);
        }

        thread.start();
    }

    Runnable task = () -> {
        while (playMode) {
            try {
                int last_x, last_y;

                ArrayList<int[]> nearPositions = new ArrayList<>();
                ArrayList<int[]> nearEmpty = new ArrayList<>();
                ArrayList<int[]> nearMineral = new ArrayList<>();
                ArrayList<int[]> nearShip = new ArrayList<>();

                last_x = this.x;
                last_y = this.y;

                Thread.sleep(750);

                if (hasMineral) {                               // LLEVA MINERAL

                    if (this.x_ship >= this.x) {
                        nearPositions.add(new int[]{x + 1, y});
                    }
                    if (this.x_ship <= this.x) {
                        nearPositions.add(new int[]{x - 1, y});
                    }
                    if (this.y_ship >= this.y) {
                        nearPositions.add(new int[]{x, y + 1});
                    }
                    if (this.y_ship <= this.y) {
                        nearPositions.add(new int[]{x, y - 1});
                    }

                    for (int[] position : nearPositions) {
                        switch (moveValidation(position)) {
                            case 0 ->
                                nearEmpty.add(position);
                            case 1 ->
                                nearShip.add(position);
                        }
                    }

                    if (!nearShip.isEmpty()) {                  // HAY NAVE CERCA
                        this.hasMineral = false;
                    } else if (!nearEmpty.isEmpty()) {          // NO HAY NAVE CERCA   
                        move(board, nearEmpty);
                   } 

                    this.board = graphic.updateBoard(last_x, last_y, this.x, this.y, hasMineral);

                } else {                                        // NO LLEVA MINERAL                

                    // VERIFICAR LAS CASILLAS CERCANAS
                    nearPositions.add(new int[]{x - 1, y});
                    nearPositions.add(new int[]{x + 1, y});
                    nearPositions.add(new int[]{x, y - 1});
                    nearPositions.add(new int[]{x, y + 1});

                    for (int[] position : nearPositions) {
                        switch (moveValidation(position)) {
                            case 0 ->
                                nearEmpty.add(position);
                            case 3 ->
                                nearMineral.add(position);
                        }
                    }

                    if (!nearMineral.isEmpty()) {               // HAY MINERALES CERCA
                        move(board, nearMineral);
                        this.hasMineral = true;
                    } else if (!nearEmpty.isEmpty()) {          // NO HAY NAVE CERCA   
                        move(board, nearEmpty);
                   } 

                    this.board = graphic.updateBoard(last_x, last_y, this.x, this.y, hasMineral);
                }

            } catch (InterruptedException e) {
                System.out.println("DIE");
            }
        }
    };

    private int[][] move(int[][] board, ArrayList<int[]> moveList) {
        int random = (int) (Math.random() * (moveList.size()));

        int local_x = moveList.get(random)[0];
        int local_y = moveList.get(random)[1];

        board[local_x][local_y] = 0;
        board[local_x][moveList.get(random)[1]] = 2;

        this.board[this.x][this.y] = 0;

        this.x = local_x;
        this.y = local_y;

        return board;
    }

    private int moveValidation(int[] nearPosition) {
        int value = 0;
        if (nearPosition[0] >= 0 && nearPosition[0] <= 9 && nearPosition[1] >= 0 && nearPosition[1] <= 9) {
            value = this.board[nearPosition[0]][nearPosition[1]];
            if (value != 2 && value != 4) {
                return value;
            }
        }
        return -1;
    }

    public void setPlayMode(boolean playMode) {
        this.playMode = playMode;
    }
}
