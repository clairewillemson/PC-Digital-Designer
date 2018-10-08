package pcdd;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * Purpose  : Create a PDF with images and product names of all the parts chosen
 * @param ImageURLWithTitle  : 2D string array with an image URL and product name for each part
 */
public class PrintPDF extends GUI {
	
	private static final long serialVersionUID = 1L;
	private String [][] ImageURLWithTitle;
	
	/**
	 * Constructor
	 */
	public PrintPDF(String [][] ImageURLWithTitle) {
		this.ImageURLWithTitle = ImageURLWithTitle;
	}
	
	/**
	 * Purpose  : Creates and opens a PDF summarizing the users build.
	 * @param args not used.
	 * @return void
	 */
    public void print() {    	    	
    	String fileDir = System.getProperty("user.dir")+"\\PCDDSummary.pdf";
        System.out.println("file at " + System.getProperty("user.dir"));
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(new File(fileDir)));
            document.open();
            Paragraph pTitle = new Paragraph();
            Font boldFont = new Font(Font.FontFamily.HELVETICA, 25, Font.BOLD);
            Phrase title = new Phrase("PC Digital Designer Build Summary", boldFont);
            pTitle.add(title);
            pTitle.setAlignment(Element.ALIGN_CENTER);
            document.add(pTitle);
            PdfPTable table = new PdfPTable(4);
            Paragraph newLine = new Paragraph();
            newLine.add("\n\n\n\n");
            document.add(newLine);
            table.setWidthPercentage(100);
            table.setWidths(new int[]{1, 2, 1, 2});
            for (int i = 0; i < 6; i++) {
	            saveImage(i);
	            table.addCell(ImageURLWithTitle[i][1] + "     ");
	            table.addCell(Image.getInstance(System.getProperty("user.dir")+"\\part" + i + ".jpg"));
            }
            document.add(table);
            document.close();
            File pdf = new File(fileDir);
            Desktop.getDesktop().open(pdf);
            cleanUp();
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }
    
    /**
	 * Purpose  : Create an image for a part out of an image URL and saves it as a JPG for use in the PDF.
	 * @param partID  : Represents the type of part that an image is being created for.
	 * @return void
	 */
    public void saveImage(int partID) throws IOException {
        URL url = new URL(ImageURLWithTitle[partID][0]);
        InputStream inStream = url.openStream();
        OutputStream outStream = new FileOutputStream(System.getProperty("user.dir")+"\\part" + partID + ".jpg");

        byte[] b = new byte[2056];
        int length;

        while ((length = inStream.read(b)) != -1) {
            outStream.write(b, 0, length);
        }

        inStream.close();
        outStream.close();
    }
    
    /**
	 * Purpose  : Remove part images from the users computer, leaving just the PDF.
	 * @param args not used.
	 * @return void
	 */
    public static void cleanUp() {
    	for (int i = 0; i < 6; i++) {
            File file = new File(System.getProperty("user.dir")+"\\part" + i + ".jpg");
            file.delete();
        }
    }
    
}
