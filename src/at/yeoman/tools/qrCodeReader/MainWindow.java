
package at.yeoman.tools.qrCodeReader;

import com.google.zxing.NotFoundException;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.io.IOException;

public class MainWindow
{
    private JTextArea textarea;
    
    public static void main(String[] args)
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> new MainWindow().run());
    }
    
    private void run()
    {
        JFrame window = new JFrame("QR Code Interpreter");
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        JButton button = createButton();
        panel.add(button, BorderLayout.NORTH);
        createTextArea();
        panel.add(textarea, BorderLayout.CENTER);
        panel.setPreferredSize(new Dimension(800, 600));
        window.setContentPane(panel);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
    }
    
    private JButton createButton()
    {
        JButton button = new JButton("paste");
        button.addActionListener(this::buttonPressed);
        return button;
    }
    
    private void createTextArea()
    {
        textarea = new JTextArea();
        textarea.setEditable(false);
        textarea.setLineWrap(true);
        textarea.setWrapStyleWord(true);
        textarea.setBorder(new LineBorder(new Color(0, true), 12));
    }
    
    private void buttonPressed(ActionEvent actionEvent)
    {
        Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.imageFlavor))
        {
            try
            {
                System.out.println("before 1");
                handleImage((Image) transferable.getTransferData(DataFlavor.imageFlavor));
                System.out.println("after 2");
            }
            catch (UnsupportedFlavorException | IOException exception)
            {
                exception.printStackTrace();
            }
        }
        else
        {
            textarea.setText("Non-image data");
        }
    }
    
    private void handleImage(Image image)
    {
        try
        {
            textarea.setText(QrCodeReader.readQrCode(image));
        }
        catch (NotFoundException exception)
        {
            exception.printStackTrace();
            textarea.setText(exception.getMessage());
        }
    }
}
