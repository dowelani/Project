import menu.Menu;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.lang.invoke.LambdaConversionException;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static java.lang.invoke.MethodHandles.lookup;
import static java.lang.invoke.MethodType.methodType;

public class MenuBuilderUtil {
    static Menu build(String xmlName) throws Throwable {
        //open an XML file and load it into memory (in a DOM Document);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        File fin = new File(xmlName);
        Document doc = builder.parse(fin);

        Element element=doc.getDocumentElement();
        NamedNodeMap attributeMap =  element.getAttributes();
        Menu menu=new Menu(attributeMap.item(1).getNodeValue());
        //create an instance of the controller specified in
        //the menu node's controller attribute using
        //reflection - this object will provide the callback
        //methods for the menu items
        Class clazz = Class.forName(attributeMap.item(0).getNodeValue());
        Controller controller = (Controller) clazz.newInstance();

        populate(doc.getDocumentElement(),0,menu,controller,true,null);
        return menu;
    }
    public static Runnable toRunnable(Method method, Object instance) throws Throwable {
        MethodHandles.Lookup lookup = lookup();

        MethodHandle test = lookup.unreflect(method);

            return (Runnable) LambdaMetafactory.metafactory(
                    lookup,
                    "run",
                    methodType(Runnable.class, instance.getClass()),
                    methodType(void.class),
                    test,
                    methodType(void.class)
            ).getTarget().invoke(instance);

    }
    public static void populate(Element element, int depth,Menu menu,Controller controller,boolean isMenu,Menu submenu) throws Throwable {

        if(element.getTagName().equals("choice")){
            NamedNodeMap attributeMap =  element.getAttributes();
            if(isMenu){
                Method method= Controller.class.getMethod(attributeMap.item(0).getNodeValue());
                menu.add(attributeMap.item(1).getNodeValue(),toRunnable(method,controller));
            }else {
                Method method= Controller.class.getMethod(attributeMap.item(0).getNodeValue());
                submenu.add(attributeMap.item(1).getNodeValue(), toRunnable(method,controller));
            }
        }
        if(element.getTagName().equals("submenu")){
            NamedNodeMap attributeMap =  element.getAttributes();
            Menu subMenu=new Menu(attributeMap.item(1).getNodeValue());
            isMenu=false;submenu=subMenu;
            menu.add(attributeMap.item(0).getNodeValue(),subMenu);
        }

        NodeList nodes = element.getChildNodes();

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) node;
                populate(childElement, depth + 1,menu,controller,isMenu,submenu);

            }
        }
    }
}
