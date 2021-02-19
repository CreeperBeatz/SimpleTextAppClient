package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static java.lang.System.exit;
import static java.lang.Thread.sleep;


public class GUI {
    private JPanel panel1;
    private JTextArea textArea1;
    private JTextField textField1;
    private JButton sendButton;
    private static Socket socket;
    private static PrintWriter output;

    public GUI() {

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                output.println(textField1.getText());
                textField1.setText("");
            }
        });
        textField1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                output.println(textField1.getText());
                textField1.setText("");
            }
        });

        AnotherTextAreaUpdater task = new AnotherTextAreaUpdater(socket, textArea1);
        task.start();
    }

    private static WindowAdapter exitAppWindowAdapter() {
        WindowAdapter exit_application = new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                JFrame frame = (JFrame) e.getSource();

                int result = JOptionPane.showConfirmDialog(
                        frame ,
                        "Are you sure you want to exit the application?" ,
                        "Exit Application" ,
                        JOptionPane.YES_NO_OPTION);

                if (result == JOptionPane.YES_OPTION) {
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                }
            }
        };
        return exit_application;
    }

    public static void main(String[] args) {

        String proxy = "2.tcp.eu.ngrok.io";//JOptionPane.showInputDialog("Enter the Proxy");
        int port = Integer.parseInt(JOptionPane.showInputDialog("Enter the Port"));

        try {
            socket = new Socket(proxy, port);

            //socket.setSoTimeout(5000);

            String username = JOptionPane.showInputDialog("Enter an Username");
            output = new PrintWriter(socket.getOutputStream(), true);
            output.println(username); //Server expects first sent line to be the username

            JFrame Gui = new JFrame("Scuffed chat - SNAPSHOT 0.5");
            Gui.setContentPane(new GUI().panel1);
            Gui.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            Gui.addWindowListener(exitAppWindowAdapter());
            Gui.pack();
            Gui.setVisible(true);

            Gui.setLocationRelativeTo(null);

        } catch (SocketTimeoutException e) {
            JOptionPane.showMessageDialog(null,
                    "Dani's server is trash\n It crashed... again",
                    "Server time out",
                    JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Invalid proxy/port!\nCouldn't connect to server!",
                    "Invalid data",
                    JOptionPane.ERROR_MESSAGE);
            exit(-2);
        }
    }
}

class AnotherTextAreaUpdater extends Thread {
    private Socket socket;
    private JTextArea jTextArea;
    private BufferedReader input;

    public AnotherTextAreaUpdater(Socket socket, JTextArea jTextArea) {
        this.socket = socket;
        this.jTextArea = jTextArea;
    }

    @Override
    public void run() {
        try {
            input = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );

            while (true) {
                String echo = input.readLine();
                if(echo != null) {
                    jTextArea.append(echo + "\n");
                    jTextArea.setCaretPosition(jTextArea.getDocument().getLength());
                }
            }
        } catch (IOException e) {
            System.out.println("fuck");
            e.printStackTrace();
        }
    }
}