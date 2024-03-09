package com.mycompany.agentereactivo;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Graphic extends JFrame {

    private int addSelection;
    private boolean start = true;

    private JButton[][] buttons = new JButton[10][10];
    private int[][] board = new int[10][10];
    private ArrayList<Collector> collectors = new ArrayList<>();

    private ImageIcon iconEmpty = new ImageIcon("Assets\\empty.png");
    private ImageIcon iconShip = new ImageIcon("Assets\\ship.png");
    private ImageIcon iconMineral = new ImageIcon("Assets\\mineral.png");
    private ImageIcon iconObstacle = new ImageIcon("Assets\\obstacle.png");
    private ImageIcon iconCollectorFull = new ImageIcon("Assets\\collectorFull.png");
    private ImageIcon iconCollectorEmpty = new ImageIcon("Assets\\collectorEmpty.png");

    public Graphic() {
        super("Agente reactivo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(660, 700);
        setResizable(false);
        setLocationRelativeTo(null);

        /*
            INIT BOARD
         */
        for (int j = 0; j < 10; j++) {
            for (int i = 0; i < 10; i++) {
                board[i][j] = 0;
            }
        }

        /*
            MENU
         */
        JMenuBar menuBar = new JMenuBar();

        JMenu addMenu = new JMenu("Add element");

        JMenuItem itemStartFunction = new JMenuItem("START");
        JMenuItem itemResetFunction = new JMenuItem("RESET");

        JMenuItem itemShipAddMenu = new JMenuItem("Ship");
        JMenuItem itemCollectorAddMenu = new JMenuItem("Collector");
        JMenuItem itemMineralsAddMenu = new JMenuItem("Minerals");
        JMenuItem itemObstaclesAddMenu = new JMenuItem("Obstacles");
        JMenuItem itemEmptyAddMenu = new JMenuItem("Empty");

        addMenu.add(itemShipAddMenu);
        addMenu.add(itemCollectorAddMenu);
        addMenu.add(itemMineralsAddMenu);
        addMenu.add(itemObstaclesAddMenu);
        addMenu.add(itemEmptyAddMenu);

        menuBar.add(itemStartFunction);
        menuBar.add(itemResetFunction);
        menuBar.add(addMenu);

        itemEmptyAddMenu.addActionListener((e) -> {
            this.addSelection = 0;
        });

        itemShipAddMenu.addActionListener((e) -> {
            this.addSelection = 1;
        });

        itemCollectorAddMenu.addActionListener((e) -> {
            this.addSelection = 2;
        });

        itemMineralsAddMenu.addActionListener((e) -> {
            this.addSelection = 3;
        });

        itemObstaclesAddMenu.addActionListener((e) -> {
            this.addSelection = 4;
        });

        /*
            START FUNCTION
         */
        itemStartFunction.addActionListener((e) -> {
            if (this.start) {
                ArrayList<int[]> collectorsPositions = new ArrayList<>();
                ArrayList<int[]> shipsPositions = new ArrayList<>();

                for (int j = 0; j < 10; j++) {
                    for (int i = 0; i < 10; i++) {
                        switch (board[i][j]) {
                            case 1 -> {
                                shipsPositions.add(new int[]{i, j});
                            }
                            case 2 -> {
                                collectorsPositions.add(new int[]{i, j});
                            }
                        }
                    }
                }

                if (shipsPositions.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "EMPTY SHIPS", "ERROR", JOptionPane.WARNING_MESSAGE);
                } else {
                    for (int[] pos : collectorsPositions) {
                        Collector collector = new Collector(this, pos[0], pos[1], shipsPositions);
                        collectors.add(collector);
                    }
                    this.start = false;
                }
            }
        });

        /*
            RESET
         */
        itemResetFunction.addActionListener((e) -> {
            try {
                for (Collector collector : collectors) {
                    collector.setPlayMode(false);
                }
                Thread.sleep(1000);
                for (int i = 0; i < 10; i++) {
                    for (int j = 0; j < 10; j++) {
                        this.board[i][j] = 0;
                        Image newImage = iconEmpty.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
                        ImageIcon newImageIcon = new ImageIcon(newImage);
                        this.buttons[i][j].setIcon(newImageIcon);
                    }
                }
                this.start = true;
            } catch (InterruptedException ex) {
                Logger.getLogger(Graphic.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        setJMenuBar(menuBar);

        /*
            BUTTOM GRID
         */
        JPanel panel = new JPanel(new GridLayout(10, 10));
        for (int j = 0; j < 10; j++) {
            for (int i = 0; i < 10; i++) {
                JButton button = new JButton();
                int x = i;
                int y = j;

                Image imgEmpty = iconEmpty.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
                button.setIcon(new ImageIcon(imgEmpty));
                button.setBorderPainted(false);
                button.setContentAreaFilled(false);
                button.setFocusPainted(false);

                // Aquí almacenamos el botón en el arreglo usando las coordenadas [i][j]
                button.addActionListener((ActionEvent e) -> {
                    board[x][y] = this.addSelection;

                    ImageIcon newIcon = iconEmpty;
                    switch (Graphic.this.addSelection) {
                        case 0 -> {
                            newIcon = iconEmpty;
                        }
                        case 1 -> {
                            newIcon = iconShip;
                        }
                        case 2 -> {
                            newIcon = iconCollectorEmpty;
                        }
                        case 3 -> {
                            newIcon = iconMineral;
                        }
                        case 4 -> {
                            newIcon = iconObstacle;
                        }
                        default ->
                            System.out.println("Botón presionado en [" + x + ", " + y + "]");
                    }
                    Image newImage = newIcon.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
                    ImageIcon newImageIcon = new ImageIcon(newImage);
                    button.setIcon(newImageIcon);
                });
                buttons[i][j] = button;

                panel.add(button);
            }

            this.add(panel);
        }

    }

    public int[][] getBoard() {
        return this.board;
    }

    public int[][] updateBoard(int last_x, int last_y, int x, int y, boolean hasMineral) {
        Image newImageCollector;
        Image newImageEmpty = iconEmpty.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);;

        this.board[last_x][last_y] = 0;
        this.board[x][y] = 2;

        if (hasMineral) {
            newImageCollector = iconCollectorFull.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
        } else {
            newImageCollector = iconCollectorEmpty.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
        }

        ImageIcon newImageIconCollector = new ImageIcon(newImageCollector);
        ImageIcon newImageIconEmpty = new ImageIcon(newImageEmpty);

        this.buttons[last_x][last_y].setIcon(newImageIconEmpty);
        this.buttons[x][y].setIcon(newImageIconCollector);

        return board;
    }
}
