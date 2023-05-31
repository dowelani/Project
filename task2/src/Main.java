import menu.Menu;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class Main {
    public static void main(String[] args) throws Throwable {

        Menu m=MenuBuilderUtil.build("menu.xml");
        m.run();
    }
}
