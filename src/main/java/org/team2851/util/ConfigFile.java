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
    /*
     *  Singleton class that opens a configuration file at the directory defined in the RobotConstants class.
     *
     *  Public/Protected Methods:
     *      getInstance(): Returns the static instance of ConfigFile
     *      getRobotName() throws NullPointerException: Returns the name of the robot defined in <Robot name="">
     *      getCANTalon(String name) throws ElementNotFoundException: Searches for the talon defined with the name
     *          provided and configures a corresponding CANTalon object. Throws an ElementNotFoundException if the
     *          element was not found in the config file or it could not be properly configured.
     *      getController(String name) throws ElementNotFoundException: Takes a configFile name and creates a controller
     *          based on the contents of the xml file
     */

    private SAXBuilder saxBuilder = new SAXBuilder();
    private Document document;

    private static ConfigFile sInstance = new ConfigFile();

    private ConfigFile()
    {
        File file = new File(RobotConstants.getInstance().configFilePath + "robot.xml");
        try {
            document = saxBuilder.build(file);
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ConfigFile getInstance() { return sInstance; }

    public String getRobotName() throws NullPointerException { return document.getRootElement().getAttributeValue("name"); }

    public TalonSRX getTalonSRX(String name) throws ElementNotFoundException
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
                if (element.getAttribute("") != null) i = element.getAttribute("i").getDoubleValue();
                if (element.getAttribute("") != null) i = element.getAttribute("i").getDoubleValue();
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

    public WPI_TalonSRX getWPI_TalonSRX(String name) throws ElementNotFoundException
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

    public int getInt(String name) throws ElementNotFoundException, DataConversionException {
        Element element = getElement("Int", name);
        return element.getAttribute("value").getIntValue();
    }

    public double getDouble(String name) throws ElementNotFoundException, DataConversionException
    {
        Element element = getElement("Double", name);
        return element.getAttribute("value").getDoubleValue();
    }

    public boolean getBoolean(String name) throws ElementNotFoundException, DataConversionException
    {
        Element element = getElement("Boolean", name);
        return element.getAttribute("value").getBooleanValue();
    }

    public PID getPid(String name) throws ElementNotFoundException, DataConversionException
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

    public static Controller getController(String configFile) throws ElementNotFoundException
    {
        File file = new File(RobotConstants.getInstance().configFilePath + configFile);
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
    private Element getElement(String name, String id) throws ElementNotFoundException
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