import org.w3c.dom.*;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        // load document and parse to create tree
        File fin = new File("hamlet.xml");
        Document doc = builder.parse(fin);

        // create xpath object that will allow document to be queried
        XPathFactory xpFactory = XPathFactory.newInstance();
        XPath path = xpFactory.newXPath();

        System.out.println("Enter name of persona:");
        Scanner in =new Scanner(System.in);
        String name= in.nextLine();
        XPathExpression result = (XPathExpression) path.compile("//PERSONA");
        NodeList resultSet = (NodeList) result.evaluate(doc, XPathConstants.NODESET);
        String resultValue="";
        for (int i = 0; i < resultSet.getLength(); i++) {
           Node node=resultSet.item(i);
           if(node.getTextContent().contains(",")){
               String[] p=node.getTextContent().split(",");
               if(p[0].equals(name.toUpperCase())){
                   resultValue=p[0];
               }
           }
           else{
               if(node.getTextContent().equals(name.toUpperCase())){
                   resultValue=node.getTextContent();
               }
           }
        }
        if(resultValue.equals(name.toUpperCase())){
            Document newDoc = createDoc();
            Element root= newDoc.createElement("lines");
            root.setAttribute("speaker",name.toUpperCase());
            newDoc.appendChild(root);
            populate(doc.getDocumentElement(),0,root,newDoc,null,null,null,0);
            saveDoc(newDoc,name.toLowerCase()+".xml");
        }
      else
         System.out.println("Entered Persona is invalid");
    }
    public static void populate(Element element, int depth,Element root,Document document,Element actElement,Element scene,Element speech,int Id) {

        if(element.getTagName().equals("ACT")){
           Element element1=document.createElement("act");
           element1.setAttribute("title", element.getFirstChild().getTextContent());
           root.appendChild(element1);actElement=element1;
        }
        if(element.getTagName().equals("SCENE")){
            Element element1=document.createElement("scene");
            element1.setAttribute("title", element.getFirstChild().getTextContent());
            actElement.appendChild(element1);scene=element1;

        }
        if(element.getTagName().equals("SPEECH")){
            Element element1=document.createElement("speech");
            element1.setAttribute("id", ""+Id);
            scene.appendChild(element1);speech=element1;

        }
        if(element.getTagName().equals("LINE")){
            Element element1=document.createElement("line");
            element1.setTextContent(element.getTextContent());
            speech.appendChild(element1);
        }

        NodeList nodes = element.getChildNodes();
        // for each node, display it (and children if any)
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);

            // is it a node containing child nodes?
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                // it is, so display the child at depth + 1
                Element childElement = (Element) node;
                if(childElement.getTagName().equals("SPEECH")){
                    Id=Id+1;
                    populate(childElement, depth + 1, root, document,actElement,scene,speech,Id);

                }else
                populate(childElement, depth + 1, root, document,actElement,scene,speech,Id);

            }
        }
    }
    public static Document createDoc() throws Exception {
        // create DocumentBuilder to parse XML document
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        // create empty document
        Document doc = builder.newDocument();

        return doc;
    }
    public static void saveDoc(Document doc, String filename) throws Exception {
        // obtain serializer
        DOMImplementation impl = doc.getImplementation();
        DOMImplementationLS implLS = (DOMImplementationLS) impl.getFeature("LS", "3.0");
        LSSerializer ser = implLS.createLSSerializer();
        ser.getDomConfig().setParameter("format-pretty-print", true);

        // create file to save too
        FileOutputStream fout = new FileOutputStream(filename);

        // set encoding options
        LSOutput lsOutput = implLS.createLSOutput();
        lsOutput.setEncoding("UTF-8");

        // tell to save xml output to file
        lsOutput.setByteStream(fout);

        // FINALLY write output
        ser.write(doc, lsOutput);

        // close file
        fout.close();
    }

}