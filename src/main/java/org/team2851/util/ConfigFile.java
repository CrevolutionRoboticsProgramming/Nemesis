package org.team2851.util;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import org.jdom2.DataConversionException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import javax.xml.crypto.Data;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class ConfigFile
{

    private static SAXBuilder saxBuilder = new SAXBuilder();
    private static Document document;

    private ConfigFile() {}

    /**
     * Imports robot.xml file. Necessary for other functions to work
     */
    public static void readFile()
    {
        File file = new File("/home/lvuser/config/robot.xml");
        try {
            document = saxBuilder.build(file);
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return The name of the robot
     * @throws NullPointerException Thrown if the name attribute is not found in the robot tag
     */
    public static String getRobotName() throws NullPointerException { return document.getRootElement().getAttributeValue("name"); }

    /**
     * Configures and returns a TalonSRX object based on a TalonSRX tag in robot.xml
     * @param name The name of the tag
     * @return TalonSRX object
     * @throws ElementNotFoundException Thrown if the tag is not found in robot.xml
     */
    public static TalonSRX getTalonSRX(String name) throws ElementNotFoundException
    {
        TalonSRX talon = null;
        Element element = getElement("TalonSRX", name);
        int port;
        boolean isInverted = false, usePID = true;
        double p = -1, i = -1, d = -1;
        Element ePid = null;

        try {
            port = element.getAttribute("port").getIntValue();
        } catch (DataConversionException e) {
            System.err.println("TalonSRX [" + name + "] could not configure port");
            return null;
        }

        try {
            isInverted = element.getAttribute("isInverted").getBooleanValue();
        } catch (DataConversionException e) { }

        List<Element> children = element.getChildren();
        for (Element e : children)
            if (e.getName().equals("PID")) ePid = e;

        if (ePid != null)
        {
            try {
                if (element.getAttribute("p") != null) p = element.getAttribute("p").getDoubleValue();
                if (element.getAttribute("i") != null) i = element.getAttribute("i").getDoubleValue();
                if (element.getAttribute("d") != null) i = element.getAttribute("i").getDoubleValue();
            } catch (DataConversionException e) {
                System.out.println("TalonSRX [" + name + "]: Failure to configure PID controller");
                usePID = false;
            }
        }
        
        talon = new TalonSRX(port);
        talon.setInverted(isInverted);
        if (usePID)
        {
            talon.selectProfileSlot(0, 0);
            talon.config_kP(0, p, 0);
            talon.config_kI(0, i, 0);
            talon.config_kD(0, d, 0);
        }

        System.out.println("TalonSRX [" + name + "] was created on port " + port + ":\n\tisInverted: true");
        return talon;
    }

    /**
     * Configures and returns a WPI_TalonSRX object based on a TalonSRX tag in robot.xml
     * @param name The name of the tag
     * @return WPI_TalonSRX object
     * @throws ElementNotFoundException Thrown if the tag is not found in robot.xml
     */
    public static WPI_TalonSRX getWPI_TalonSRX(String name) throws ElementNotFoundException
    {
        WPI_TalonSRX talon;
        Element e = getElement("TalonSRX", name);
        Element ePID = null;
        List<Element> children = e.getChildren();
        int port;
        boolean isInverted = false;
        double kP, kI, kD;

        try {
            port = e.getAttribute("port").getIntValue();
        } catch (DataConversionException e1) {
            Logger.printerr("TalonSRX [" + name + "]: Could not parse port!");
            throw new ElementNotFoundException();
        }

        try {
            isInverted = e.getAttribute("isInverted").getBooleanValue();
            Logger.println("TalonSRX [" + name + "]: isInverted = " + isInverted);
        } catch (DataConversionException e1) { }

        talon = new WPI_TalonSRX(port);
        talon.setInverted(isInverted);

        for (Element el : children)
            if (el.getName().equals("PID")) ePID = el;

        if (ePID != null)
        {
            try {
                kP = ePID.getAttribute("p").getDoubleValue();
                kI = ePID.getAttribute("i").getDoubleValue();
                kD = ePID.getAttribute("d").getDoubleValue();

                talon.config_kP(0, kP, 0);
                talon.config_kI(0, kI, 0);
                talon.config_kD(0, kD, 0);
            } catch (DataConversionException e1) {
                Logger.printerr("TalonSRX [" + name + "]: Failed to parse PID");
            }
        }

        return talon;
    }

    /**
     * Returns integer constant from robot.xml
     * @param name The name of the tag
     * @return The integer value
     * @throws ElementNotFoundException Thrown if the tag is not found in robot.xml
     * @throws DataConversionException Thrown if the value attribute does not contain an integer
     */
    public static int getInt(String name) throws ElementNotFoundException, DataConversionException {
        Element element = getElement("Int", name);
        return element.getAttribute("value").getIntValue();
    }

    /**
     * Returns double constant from robot.xml
     * @param name The name of the tag
     * @return The double value
     * @throws ElementNotFoundException Thrown if the tag is not found in robot.xml
     * @throws DataConversionException Thrown if the value attribute does not contain a double
     */
    public static double getDouble(String name) throws ElementNotFoundException, DataConversionException
    {
        Element element = getElement("Double", name);
        return element.getAttribute("value").getDoubleValue();
    }

    /**
     * Returns boolean constant from robot.xml
     * @param name The name of the tag
     * @return The boolean value
     * @throws ElementNotFoundException Thrown if the tag is not found in robot.xml
     * @throws DataConversionException Thrown if the value attribute does not contain a boolean
     */
    public static boolean getBoolean(String name) throws ElementNotFoundException, DataConversionException
    {
        Element element = getElement("Boolean", name);
        return element.getAttribute("value").getBooleanValue();
    }

    /**
     * Returns a PID object configured by the tag
     * @param name The name of the tag
     * @return The PID object
     * @throws ElementNotFoundException Thrown if the tag is not found in robot.xml
     * @throws DataConversionException Thrown if the P, I, or D values are not doubles
     */
    public static PID getPid(String name) throws ElementNotFoundException, DataConversionException
    {
        double p, i, d;
        Element element = getElement("PID", name);
        if (element.getAttribute("p") != null) p = element.getAttribute("p").getDoubleValue();
        else return null;

        if (element.getAttribute("i") != null) i = element.getAttribute("i").getDoubleValue();
        else return new PID(p);

        if (element.getAttribute("d") != null) d = element.getAttribute("d").getDoubleValue();
        else return new PID(p, i);

        return new PID(p, i, d);
    }

    /**
     * Returns a configured controller object
     * @param configFile The absolute path of the Controller ConfigFile
     * @return Controller object
     * @throws ElementNotFoundException Thrown if a required element is not found
     */
    public static Controller getController(String configFile) throws ElementNotFoundException
    {
        File file = new File("/home/lvuser/config/" + configFile);
        Document doc = null;
        try {
            doc = new SAXBuilder().build(file);
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Controller controller;

        Element rootElement;
        rootElement = doc.getRootElement();

        try {
            controller = new Controller(rootElement.getAttribute("port").getIntValue());
        } catch (DataConversionException e) {
            System.err.println("Controller could not be configured, port could not be parsed.");
            throw new ElementNotFoundException();
        } catch (NullPointerException e) {
            System.err.println("Controller could not be configured, port was not defined.");
            throw new ElementNotFoundException();
        }

        List<Element> buttonElements = rootElement.getChildren("Button");
        List<Element> axisElements = rootElement.getChildren("Axis");

        for (Element e : buttonElements)
        {
            String button;
            try {
                button = e.getAttributeValue("id");

            } catch (NullPointerException ex) {
                System.err.println("Button id was not defined, button will not be initialized");
                continue;
            }

            Controller.ButtonMode mode = null;
            try {
                if (e.getAttributeValue("mode").equals("raw")) {
                    mode = Controller.ButtonMode.Raw;
                }
                else if (e.getAttributeValue("mode").equals("toggle")) {
                    mode = Controller.ButtonMode.Toggle;
                }
                else {
                    mode = Controller.ButtonMode.Raw;
                    System.err.println("Invalid button mode for a, setting to raw");
                }
            } catch (NullPointerException ex) {
                System.err.println("Button mode not defined, setting to raw");
                mode = Controller.ButtonMode.Raw;
            }

            switch (button)
            {
                case "a":
                {
                    Controller.ButtonID id = Controller.ButtonID.A;
                    controller.a = new Controller.Button(id, mode, controller.joystick);
                    break;
                }

                case "b":
                {
                    Controller.ButtonID id = Controller.ButtonID.B;
                    controller.b = new Controller.Button(id, mode, controller.joystick);
                    break;
                }

                case "x":
                {
                    Controller.ButtonID id = Controller.ButtonID.X;
                    controller.x = new Controller.Button(id, mode, controller.joystick);
                    break;
                }

                case "y":
                {
                    Controller.ButtonID id = Controller.ButtonID.Y;
                    controller.y = new Controller.Button(id, mode, controller.joystick);
                    break;
                }

                case "start":
                {
                    Controller.ButtonID id = Controller.ButtonID.START;
                    controller.start = new Controller.Button(id, mode, controller.joystick);
                    break;
                }

                case "select":
                {
                    Controller.ButtonID id = Controller.ButtonID.SELECT;
                    controller.select = new Controller.Button(id, mode, controller.joystick);
                    break;
                }

                case "leftBumper":
                {
                    Controller.ButtonID id = Controller.ButtonID.LEFT_BUMPER;
                    controller.leftBumper = new Controller.Button(id, mode, controller.joystick);
                    break;
                }

                case "rightBumper":
                {
                    Controller.ButtonID id = Controller.ButtonID.RIGHT_BUMPER;
                    controller.rightBumper = new Controller.Button(id, mode, controller.joystick);
                    break;
                }

                case "leftJoy":
                {
                    Controller.ButtonID id = Controller.ButtonID.LEFT_JOY;
                    controller.leftJoy = new Controller.Button(id, mode, controller.joystick);
                    break;
                }

                case "rightJoy":
                {
                    Controller.ButtonID id = Controller.ButtonID.RIGHT_JOY;
                    controller.rightJoy = new Controller.Button(id, mode, controller.joystick);
                    break;
                }

                default:
                {
                    System.err.println("Invalid button id, not initializing button");
                    break;
                }
            }
        }

        for (Element e : axisElements) {
            String axis;
            try {
                axis = e.getAttributeValue("id");

            } catch (NullPointerException ex) {
                System.err.println("Axis id was not defined, button will not be initialized");
                continue;
            }

            Controller.AxisMode mode = null;
            try {
                if (e.getAttributeValue("mode").equals("inverted")) {
                    mode = Controller.AxisMode.Inverted;
                } else {
                    if (!e.getAttributeValue("mode").equals("raw"))
                        System.err.println("Invalid axis mode, setting mode to raw");
                    mode = Controller.AxisMode.Inverted;
                }
            } catch (NullPointerException ex) {
                System.err.println("Axis mode not defined, setting to raw");
                mode = Controller.AxisMode.Raw;
            }

            switch (axis) {
                case "leftX": {
                    Controller.AxisID id = Controller.AxisID.LEFT_X;
                    controller.leftX = new Controller.Axis(id, mode, controller.joystick);
                    break;
                }

                case "leftY": {
                    Controller.AxisID id = Controller.AxisID.LEFT_Y;
                    controller.leftY = new Controller.Axis(id, mode, controller.joystick);
                    break;
                }

                case "rightX": {
                    Controller.AxisID id = Controller.AxisID.RIGHT_X;
                    controller.rightX = new Controller.Axis(id, mode, controller.joystick);
                    break;
                }

                case "rightY": {
                    Controller.AxisID id = Controller.AxisID.RIGHT_Y;
                    controller.rightY = new Controller.Axis(id, mode, controller.joystick);
                    break;
                }

                case "rightTrigger": {
                    Controller.AxisID id = Controller.AxisID.RIGHT_TRIGGER;
                    controller.rightTrigger = new Controller.Axis(id, mode, controller.joystick);
                    break;
                }

                case "leftTrigger": {
                    Controller.AxisID id = Controller.AxisID.LEFT_TRIGGER;
                    controller.leftTrigger = new Controller.Axis(id, mode, controller.joystick);
                    break;
                }

                default: {
                    System.err.println("Invalid axis id, not initializing button");
                    continue;
                }
            }

        }
        return controller;
    }
    // TODO: [Change]: Added tag filter and removed root element argument
    private static Element getElement(String name, String id) throws ElementNotFoundException
    {
        List<Element> elements = document.getRootElement().getChildren();
        for (Element e : elements)
        {
            if (!e.getName().equals(name)) continue;
            if (e.getAttribute("name").getValue().equals(id)) return e;
        }
        throw new ElementNotFoundException();
    }
}