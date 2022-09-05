package ua.edu.yarik;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ProgramB {
    public static void main(String[] args) {
        WindowB window = new WindowB();
    }
}


class WindowB{
    private Semaphore semaphore = new Semaphore();
    private Thread th1;
    private Thread th2;
    private JFrame window;
    private JSlider slider;
    private JButton buttonStart1;
    private JButton buttonStart2;
    private JButton buttonStop1;
    private JButton buttonStop2;
    private JLabel labelSemaphore;

    WindowB(){
        setupUi();

        // add event listener for
        buttonStart1.addActionListener(
                (ActionEvent e) -> {
                    if (semaphore.isClosed()){
                        System.out.println("Can't start thread-1: because closed");
                        return;
                    }

                    closeSemaphore();
                    buttonStop2.setEnabled(false);
                    this.th1 = new Thread(new ThreadChangeSliderValue(slider, 10));
                    th1.start();
                });

        buttonStart2.addActionListener(
                (ActionEvent e) -> {
                    if (semaphore.isClosed()){
                        System.out.println("Can't start thread-2: because closed");
                        return;
                    }

                    closeSemaphore();
                    buttonStop1.setEnabled(false);
                    this.th2 = new Thread(new ThreadChangeSliderValue(slider, 90));
                    th2.start();
                });

        buttonStop1.addActionListener(
                (ActionEvent e) -> {
                    if(th1 == null){
                        return;
                    }
                    th1.interrupt();
                    th1 = null;
                    openSemaphore();
                    buttonStop2.setEnabled(true);
                });

        buttonStop2.addActionListener(
                (ActionEvent e) -> {
                    if(th2 == null){
                        return;
                    }
                    th2.interrupt();
                    th2 = null;
                    openSemaphore();
                    buttonStop1.setEnabled(true);
                });
    }



    private void setupUi(){
        this.window = new JFrame("Lab 1b");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(400,400);
        window.setLocation(new Point(500, 200));


        // build slider
        this.slider = new JSlider(0,100,50);
        slider.setMajorTickSpacing(10);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setForeground(Color.black);
        Font font = new Font("Serif", Font.BOLD, 16);
        slider.setFont(font);
        slider.setPreferredSize(new Dimension(300, 100));

        // build buttons
        this.buttonStart1 = new JButton("Start 1");
        this.buttonStart2 = new JButton("Start 2");
        this.buttonStop1 = new JButton("Stop 1");
        this.buttonStop2 = new JButton("Stop 2");
        buttonStart1.setPreferredSize(new Dimension(100, 50));
        buttonStart2.setPreferredSize(new Dimension(100, 50));
        buttonStop1.setPreferredSize(new Dimension(100, 50));
        buttonStop2.setPreferredSize(new Dimension(100, 50));

        // build label
        labelSemaphore = new JLabel();
        openSemaphore();
        labelSemaphore.setFont(new Font("Serif", Font.BOLD, 30));

        // make layout
        JPanel panelMain = new JPanel();
        panelMain.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;

        panelMain.add(slider, c);
        c.gridx = 0;
        c.gridy = 1;
        c.insets = new Insets(1, 1,10,1);
        panelMain.add(labelSemaphore, c);
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 1;
        c.insets = new Insets(0, 0, 10, 0);
        c.anchor = GridBagConstraints.LINE_END;
        panelMain.add(buttonStart1, c);
        c.gridx = 1;
        c.gridy = 2;
        panelMain.add(buttonStart2, c);
        c.gridx = 0;
        c.gridy = 3;
        panelMain.add(buttonStop1, c);
        c.gridx = 1;
        c.gridy = 3;
        panelMain.add(buttonStop2, c);


        window.setContentPane(panelMain);
        window.setVisible(true);
    }
    private void openSemaphore(){
        semaphore.open();
        labelSemaphore.setText("Open");
        labelSemaphore.setForeground(Color.green);
    }
    private void closeSemaphore(){
        semaphore.close();
        labelSemaphore.setText("Closed");
        labelSemaphore.setForeground(Color.RED);
    }
}


class Semaphore{
    private int value = 1;

    public synchronized boolean isOpen(){
        return value == 1;
    }

    public synchronized boolean isClosed(){
        return value == 0;
    }

    public synchronized void open(){
        this.value = 1;
    }

    public synchronized void close(){
        this.value = 0;
    }
}
