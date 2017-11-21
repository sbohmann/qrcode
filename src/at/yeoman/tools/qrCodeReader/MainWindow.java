
package at.yeoman.tools.qrCodeReader;

import com.google.zxing.NotFoundException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

class MainWindow
{
    private static final String ApplicationName = "QR Code Interpreter";
    private static final Color Transparent = new Color(0, true);
    
    private volatile File lastDirectory;
    
    private JFrame window;
    private JTextArea textarea;
    private ImageView imageView;
    
    public static void main(String[] args)
    {
        try
        {
            System.setProperty("apple.awt.application.name", ApplicationName);
    
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
        
        window = new JFrame(ApplicationName);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        JPanel toolbar = new JPanel();
        panel.add(toolbar, BorderLayout.NORTH);
        toolbar.add(createPasteButton());
        toolbar.add(createLoadButton());
        toolbar.add(createClearButton());
        createTextArea();
        panel.add(textarea, BorderLayout.CENTER);
        createImageView();
        panel.add(imageView, BorderLayout.EAST);
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
    
    private JButton createClearButton()
    {
        JButton button = new JButton("clear");
        button.addActionListener(this::clearButtonPressed);
        return button;
    }
    
    private void createTextArea()
    {
        textarea = new JTextArea();
        textarea.setEditable(false);
        textarea.setLineWrap(true);
        textarea.setWrapStyleWord(true);
        textarea.setBorder(new LineBorder(Transparent, 12));
    }
    
    private void createImageView()
    {
        imageView = new ImageView();
        imageView.setBackground(Color.black);
        imageView.setOpaque(true);
        imageView.setPreferredSize(new Dimension(300, 300));
    }
    
    private void pasteButtonPressed(ActionEvent actionEvent)
    {
        imageView.setImage(null);
    
        Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.imageFlavor))
        {
            try
            {
                Image image = (Image) transferable.getTransferData(DataFlavor.imageFlavor);
                handleImage(createBufferedImage(image));
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
            imageView.setImage(null);
            
            lastDirectory = fileChooser.getCurrentDirectory();
            
            File selectedFile = fileChooser.getSelectedFile();
            if (selectedFile != null && selectedFile.isFile())
            {
                loadImageFile(selectedFile);
            }
        }
    }
    
    private void clearButtonPressed(ActionEvent actionEvent)
    {
        imageView.setImage(null);
        textarea.setText("");
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
            BufferedImage image = ImageIO.read(selectedFile);
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
    
    private void handleImage(BufferedImage image)
    {
        imageView.setImage(image);
        
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
    
    private static BufferedImage createBufferedImage(Image image)
    {
        if (image instanceof BufferedImage)
        {
            return (BufferedImage) image;
        }
        else
        {
            BufferedImage result = new BufferedImage(
                image.getWidth(null),
                image.getHeight(null),
                BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = result.createGraphics();
            graphics.drawImage(image, 0, 0, null);
            graphics.dispose();
            return result;
        }
    }
}
