
package at.yeoman.tools.qrCodeReader;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

class ImageView extends JPanel
{
    private BufferedImage image;
    
    @Override
    public void paint(Graphics g)
    {
        super.paint(g);
        
        if (image != null && image.getWidth() > 0 && image.getHeight() > 0)
        {
            paintImage((Graphics2D) g);
        }
    }
    
    private void paintImage(Graphics2D g)
    {
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        
        double width = image.getWidth();
        double height = image.getHeight();
        
        double ratioForWidth = inset(getWidth()) / width;
        double ratioForHeight = inset(getHeight()) / height;
        double ratio = ratioForWidth < ratioForHeight ? ratioForWidth : ratioForHeight;
        
        width *= ratio;
        height *= ratio;
        
        double x = (getWidth() - width) / 2.0;
        double y = (getHeight() - height) / 2.0;
        
        AffineTransform transform = new AffineTransform();
        transform.translate(x, y);
        transform.scale(ratio, ratio);
        
        g.drawRenderedImage(image, transform);
    }
    
    private int inset(int width)
    {
        return Math.max(width - 24, 0);
    }
    
    void setImage(BufferedImage image)
    {
        this.image = image;
        repaint();
    }
}
