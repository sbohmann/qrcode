
package at.yeoman.tools.qrCodeReader;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import java.awt.*;
import java.awt.image.BufferedImage;

class QrCodeReader
{
    static String readQrCode(Image image) throws NotFoundException
    {
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(
            new BufferedImageLuminanceSource(createBufferedImage(image))));
        Result qrCodeResult = new MultiFormatReader().decode(binaryBitmap);
        return qrCodeResult.getText();
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
