package ua.edu.yarik;

import javax.swing.*;
import javax.swing.border.Border;
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
    private Thread th1;
    private Thread th2;
    WindowA(){
        JFrame win = new JFrame("Window");
        win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        win.setSize(1400,500);


        // build slider
        JSlider slider = new JSlider(0,100,50);
        slider.setMajorTickSpacing(10);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setForeground(Color.black);
        Font font = new Font("Serif", Font.BOLD, 16);
        slider.setFont(font);
        slider.setPreferredSize(new Dimension(300, 100));

        // build spinners
        JSpinner spinner1 = new JSpinner(new SpinnerNumberModel(5, 1, 10, 1));
        JSpinner spinner2 = new JSpinner(new SpinnerNumberModel(5, 1, 10, 1));
        spinner1.setPreferredSize(new Dimension(100, 50));
        spinner2.setPreferredSize(new Dimension(100, 50));

        JButton button = new JButton("Start!");

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
        panelMain.add(spinner1, c);
        c.gridx = 1;
        c.gridy = 1;
        panelMain.add(spinner2, c);
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2;
        panelMain.add(button, c);

        win.setContentPane(panelMain);
        win.setVisible(true);

        // add event listener for
        button.addActionListener(
            (ActionEvent e) -> {
                if (th1 != null){
                    return;
                }
                this.th1 = new Thread(new ThreadChangeSliderValue(slider, 10));
                this.th2 = new Thread(new ThreadChangeSliderValue(slider, 90));

                spinner1.setValue(5);
                spinner2.setValue(5);

                spinner1.addChangeListener(new SpinnerValueChangedListener(spinner1, this.th1));
                spinner2.addChangeListener(new SpinnerValueChangedListener(spinner2, this.th2));

                th1.start();
                th2.start();
            });





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
        while(true){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
//            synchronized (slider){
                for (int i = 0; i < 10; i++){
                    synchronized (slider){
                        int curValue = (int) (slider.getValue());
                        int valueToSet = this.value == 10 ? --curValue : ++curValue;
                        valueToSet = putInBounds(valueToSet, 10, 90);
                        slider.setValue(valueToSet);
                    }
                }
                slider.setValue(value);
                System.out.println("Changed value to " + value);
 //           }
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
                        System.out.println("Changed priority to " + newPriority);
                    }
                    else{
                        ((JSpinner) source).setValue(1);
                    }

                }

            }
            else{
                ((JSpinner) source).setValue(1);
            }
        }
    }
}
