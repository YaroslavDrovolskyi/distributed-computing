package ua.edu.yarik;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ProgramA {

    public static void main(String[] args) {
        WindowA w = new WindowA();
    }
}

class WindowA{
    private boolean isStarted = false;
    private Thread th1;
    private Thread th2;
    private JFrame window;
    private JSlider slider;
    private JSpinner spinnerTh1;
    private JSpinner spinnerTh2;
    private JButton buttonStart;

    WindowA(){
        setupUi();

        // add event listener for
        buttonStart.addActionListener(
            (ActionEvent e) -> {
                if (isStarted){
                    return;
                }
                this.th1 = new Thread(new ThreadChangeSliderValue(slider, 10));
                this.th2 = new Thread(new ThreadChangeSliderValue(slider, 90));

                spinnerTh1.setValue(5);
                spinnerTh2.setValue(5);

                spinnerTh1.addChangeListener(new SpinnerValueChangedListener(spinnerTh1, this.th1));
                spinnerTh2.addChangeListener(new SpinnerValueChangedListener(spinnerTh2, this.th2));

                th1.start();
                th2.start();
                isStarted = true;
            });
    }



    private void setupUi(){
        this.window = new JFrame("Lab 1a");
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

        // build spinners
        this.spinnerTh1 = new JSpinner(new SpinnerNumberModel(5, 1, 10, 1));
        this.spinnerTh2 = new JSpinner(new SpinnerNumberModel(5, 1, 10, 1));
        spinnerTh1.setPreferredSize(new Dimension(100, 50));
        spinnerTh2.setPreferredSize(new Dimension(100, 50));

        this.buttonStart = new JButton("Start!");

        // make layout
        JPanel panelMain = new JPanel();
        panelMain.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.CENTER;
        panelMain.add(slider, c);
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        panelMain.add(spinnerTh1, c);
        c.gridx = 1;
        c.gridy = 1;
        panelMain.add(spinnerTh2, c);
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2;
        panelMain.add(buttonStart, c);

        window.setContentPane(panelMain);
        window.setVisible(true);
    }
}

class ThreadChangeSliderValue implements Runnable{
    private JSlider slider;
    private final int value;

    ThreadChangeSliderValue(JSlider slider, int value){
        this.slider = slider;
        this.value = value;
    }
    @Override
    public void run() {
        while(Thread.interrupted() == false){ // do while thread isn't interrupted
            synchronized (slider){
                int curValue = (int) (slider.getValue()); // increment or decrement slider
                int newValue = this.value == 10 ? --curValue : ++curValue;
                newValue = putInBounds(newValue, 10, 90);
                slider.setValue(newValue);

                // sleep for 10 msec
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                //    throw new RuntimeException(e);
                    System.out.println("Interrupted thread during sleep()");
                    break;
                }
                System.out.println("Changed slider value to " + newValue);
            }
        }
    }

    private int putInBounds(int val, int low, int high){
        if (val < low){
            return low;
        }
        if (val > high){
            return high;
        }
        return val;
    }
}

class SpinnerValueChangedListener implements ChangeListener {
    private JSpinner spinner;
    private Thread thread;
    SpinnerValueChangedListener(JSpinner spinner, Thread thread){
        this.spinner = spinner;
        this.thread = thread;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        Object source = e.getSource();
        if (source instanceof JSpinner){
            Object newValue = ((JSpinner)source).getValue();
            if (newValue instanceof Integer){
                int newPriority = (int)(((JSpinner)source).getValue());
                if (newPriority >= 1 && newPriority <= 10){
                    if (this.thread != null){
                        this.thread.setPriority(newPriority);
                        System.out.println("Changed priority for " + this.thread.toString() + " to " + newPriority);
                    }
                    else{
                        ((JSpinner) source).setValue(1);
                    }

                }

            }
            else{
                ((JSpinner) source).setValue(5);
                this.thread.setPriority(5);
            }
        }
    }
}
