
package at.yeoman.tools.qrCodeReader;

import com.google.zxing.NotFoundException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

class MainWindow
{
    private volatile File lastDirectory;
    
    private JFrame window;
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
        lastDirectory = PreferenceStorage.load();
        
        System.out.println("Last directory: " + lastDirectory);
        
        window = new JFrame("QR Code Interpreter");
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        JPanel toolbar = new JPanel();
        panel.add(toolbar, BorderLayout.NORTH);
        JButton pasteButton = createPasteButton();
        toolbar.add(pasteButton);
        JButton loadButton = createLoadButton();
        toolbar.add(loadButton);
        createTextArea();
        panel.add(textarea, BorderLayout.CENTER);
        panel.setPreferredSize(new Dimension(800, 600));
        window.setContentPane(panel);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
    
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                PreferenceStorage.save(lastDirectory);
            }
        });
    }
    
    private JButton createPasteButton()
    {
        JButton button = new JButton("paste");
        button.addActionListener(this::pasteButtonPressed);
        return button;
    }
    
    private JButton createLoadButton()
    {
        JButton button = new JButton("load");
        button.addActionListener(this::loadButtonPressed);
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
    
    private void pasteButtonPressed(ActionEvent actionEvent)
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
    
    private void loadButtonPressed(ActionEvent actionEvent)
    {
        JFileChooser fileChooser = createFileChooser();
        if (fileChooser.showOpenDialog(window) == JFileChooser.APPROVE_OPTION)
        {
            lastDirectory = fileChooser.getCurrentDirectory();
            
            File selectedFile = fileChooser.getSelectedFile();
            if (selectedFile != null && selectedFile.isFile())
            {
                loadImageFile(selectedFile);
            }
        }
    }
    
    private JFileChooser createFileChooser()
    {
        if (lastDirectory != null && lastDirectory.isDirectory())
        {
            return new JFileChooser(lastDirectory);
        }
        else
        {
            return new JFileChooser();
        }
    }
    
    private void loadImageFile(File selectedFile)
    {
        try
        {
            Image image = ImageIO.read(selectedFile);
            if (image != null)
            {
                handleImage(image);
            }
            else
            {
                textarea.setText("Unsupported file format\n\n" + selectedFile.getAbsolutePath());
            }
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
            textarea.setText("Unable to load image\n\n" + exception.getMessage());
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
            textarea.setText("No QR code found in image");
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
            textarea.setText("Unable to read QR code\n\n" + exception.getMessage());
        }
    }
}
