package summaryGenerator.ooxml;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xwpf.usermodel.Document;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlToken;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTInline;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class CustomXWPFDocument extends XWPFDocument {

    public CustomXWPFDocument() {
        super();
    }

    public CustomXWPFDocument(InputStream in) throws IOException {
        super(in);
    }

    public void addPicture(BufferedImage image, int width, int height) throws InvalidFormatException, FileNotFoundException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "PNG", out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        addPicture(in, Document.PICTURE_TYPE_PNG, width, height);
    }

    public void addPicture(String filename, int type, int width, int height) throws InvalidFormatException, FileNotFoundException {
        addPicture(new FileInputStream(filename), type, width, height);
    }

    public void addPicture(InputStream inputStream, int type, int width, int height) throws InvalidFormatException, FileNotFoundException {
        String id = addPictureData(inputStream, type);
        createPicture(id, getNextPicNameNumber(type), width, height);
    }

    private void createPicture(String blipId, int id, int width, int height) {
        final int EMU = 9525;
        width *= EMU;
        height *= EMU;
        CTInline inline = createParagraph().createRun().getCTR().addNewDrawing().addNewInline();

        String picXml = "" + "<a:graphic xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\">"
                + "   <a:graphicData uri=\"http://schemas.openxmlformats.org/drawingml/2006/picture\">"
                + "      <pic:pic xmlns:pic=\"http://schemas.openxmlformats.org/drawingml/2006/picture\">" + "         <pic:nvPicPr>"
                + "            <pic:cNvPr id=\""
                + id
                + "\" name=\"Generated\"/>"
                + "            <pic:cNvPicPr/>"
                + "         </pic:nvPicPr>"
                + "         <pic:blipFill>"
                + "            <a:blip r:embed=\""
                + blipId
                + "\" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\"/>"
                + "            <a:stretch>"
                + "               <a:fillRect/>"
                + "            </a:stretch>"
                + "         </pic:blipFill>"
                + "         <pic:spPr>"
                + "            <a:xfrm>"
                + "               <a:off x=\"0\" y=\"0\"/>"
                + "               <a:ext cx=\""
                + width
                + "\" cy=\""
                + height
                + "\"/>"
                + "            </a:xfrm>"
                + "            <a:prstGeom prst=\"rect\">"
                + "               <a:avLst/>"
                + "            </a:prstGeom>"
                + "         </pic:spPr>" + "      </pic:pic>" + "   </a:graphicData>" + "</a:graphic>";

        XmlToken xmlToken = null;
        try {
            xmlToken = XmlToken.Factory.parse(picXml);
        } catch (XmlException xe) {
            xe.printStackTrace();
        }
        inline.set(xmlToken);

        inline.setDistT(0);
        inline.setDistB(0);
        inline.setDistL(0);
        inline.setDistR(0);

        CTPositiveSize2D extent = inline.addNewExtent();
        extent.setCx(width);
        extent.setCy(height);

        CTNonVisualDrawingProps docPr = inline.addNewDocPr();
        docPr.setId(id);
        docPr.setName("Picture " + id);
        docPr.setDescr("Generated");
    }

}
