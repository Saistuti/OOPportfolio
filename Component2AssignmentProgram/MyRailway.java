import uk.ac.leedsbeckett.oop.*;
import javax.swing.*;
import java.io.*;
import java.util.*;

// Requirement 1:
// MyRailway class extends OOPrailwaySim
public class MyRailway extends OOPrailwaySim
{
    // Requirement 4:
    // Store command history for save/load
    private ArrayList<String> commandHistory = new ArrayList<>();

    private Map<Integer, Integer> locoSpeeds = new HashMap<>();
    private List<Integer> activeLocoIds = new ArrayList<>();
    private int totalLocomotives = 0;   // Stats counter

    // NEW FIELD FOR STATS
    private int totalCommands = 0;

    // Constructor
    public MyRailway()
    {
        // Setup simulator
        super();

        // Make simulator visible
        setVisible(true);

        System.out.println("Railway Simulator Started");

        // Requirement 1:
        // Display about screen
        about();
    }

    // Requirement 5:
    // Override about method
    @Override
    public void about()
    {
        // Call original about method
        super.about();

        // Add your own message
        JOptionPane.showMessageDialog(this, "Railway Simulator\nCreated by Sai Stuti Subedi", "About", JOptionPane.INFORMATION_MESSAGE);
    }

    // Requirement 1:
    // Automatically called when user presses ENTER
    @Override
    public void processCommand(String command)
    {
        // Convert command to lowercase
        command = command.toLowerCase().trim();

        // Store commands
        if(!command.equals(""))
        {
            commandHistory.add(command);
            totalCommands++;  // NEW: Track total commands
        }

        // Execute command
        executeCommand(command);
    }

    // Requirement 4:
    // Separate command processing method
    public void executeCommand(String command)
    {
        // Split command into parts
        String[] parts = command.split(" ");

        try
        {
            switch(parts[0]) {
                // Requirement 1
                case "about":
                    about();
                    break;

                // Requirement 2
                case "start":
                    startSimulation();
                    break;

                case "stop":
                    stopSimulation();
                    break;

                // Requirement 2 - FIXED RESET
                case "reset":
                    resetSimulation();

                    // NEW: Complete reset of all tracking variables
                    totalLocomotives = 0;
                    totalCommands = 0;
                    activeLocoIds.clear();
                    locoSpeeds.clear();
                    commandHistory.clear();

                    JOptionPane.showMessageDialog(this, "🔄 Simulation Completely Reset!\nAll trains cleared and counters reset.");
                    System.out.println("Full Reset Complete");
                    break;

                // Requirement 2
                // addloco id x y
                case "addloco":
                    if (parts.length < 4) {
                        System.out.println("Missing Parameters");
                        return;
                    }

                    int locoId = Integer.parseInt(parts[1]);
                    int x = Integer.parseInt(parts[2]);
                    int y = Integer.parseInt(parts[3]);

                    // Add locomotive
                    addLocomotive(new Locomotive(getWorld(), x, y));
                    totalLocomotives++;  // NEW: Track total locomotives
                    activeLocoIds.add(locoId);  // NEW: Track active locos

                    System.out.println("Locomotive Added");

                    break;

                case "addslowloco":
                    if (parts.length < 3) {
                        System.out.println("Missing Parameters");
                        return;
                    }

                    x = Integer.parseInt(parts[1]);
                    y = Integer.parseInt(parts[2]);

                    // create NewLoco with slow speed
                    int newId = addLocomotive(new NewLoco(getWorld(), x, y, 0, x + y));

                    // set slow speed (adjust value if needed)
                    setLocomotiveSpeed(newId, 1);

                    System.out.println("Slow NewLoco added at (" + x + "," + y + ")");
                    break;

                // Requirement 2
                // attachcarriage id
                case "attachcarriage":
                    if (parts.length < 2) {
                        System.out.println("Missing Parameter");
                        return;
                    }

                    int attachId = Integer.parseInt(parts[1]);
                    addCarriageToLocomotive(attachId, new Carriage(getWorld()));
                    System.out.println("Carriage Attached");
                    break;

                // Requirement 2
                // detachcarriage id
                case "detachcarriage":
                    if (parts.length < 2) {
                        System.out.println("Missing Parameter");
                        return;
                    }

                    int detachId = Integer.parseInt(parts[1]);
                    detachCarriageFromLocomotive(detachId);
                    System.out.println("Carriage Detached");
                    break;

                // Requirement 2
                // speed id speed
                case "speed":
                    if (parts.length < 3) {
                        System.out.println("Missing Parameters");
                        return;
                    }

                    int speedId = Integer.parseInt(parts[1]);
                    int speed = Integer.parseInt(parts[2]);
                    setLocomotiveSpeed(speedId, speed);
                    System.out.println("Speed Changed");
                    break;

                // Requirement 2
                // addjunction id x y
                case "addjunction":
                    if (parts.length < 4) {
                        System.out.println("Missing Parameters");
                        return;
                    }

                    int junctionId = Integer.parseInt(parts[1]);
                    int jx = Integer.parseInt(parts[2]);
                    int jy = Integer.parseInt(parts[3]);

                    // Simulated junction creation
                    JOptionPane.showMessageDialog(this, "Junction " + junctionId + " added at (" + jx + "," + jy + ")");
                    System.out.println("Junction Added");
                    break;

                // Requirement 2
                // setjunction id value
                case "setjunction":
                    if (parts.length < 3) {
                        System.out.println("Missing Parameters");
                        return;
                    }

                    int crossingId = Integer.parseInt(parts[1]);
                    int value = Integer.parseInt(parts[2]);

                    // The library uses crossings
                    toggleCrossing(crossingId);
                    JOptionPane.showMessageDialog(this, "Junction " + crossingId + " set to value " + value);
                    System.out.println("Junction Changed");
                    break;

                // Requirement 5
                // train id carriage x y
                case "train":
                    if (parts.length < 5) {
                        System.out.println("Missing Parameters");
                        return;
                    }

                    int trainId = Integer.parseInt(parts[1]);
                    int carriageCount = Integer.parseInt(parts[2]);
                    int tx = Integer.parseInt(parts[3]);
                    int ty = Integer.parseInt(parts[4]);

                    // Add train
                    newId = addLocomotive(new Locomotive(getWorld(), tx, ty));
                    totalLocomotives++;  // NEW: Track total locomotives
                    activeLocoIds.add(newId);  // NEW: Track active locos

                    // Automatically add carriages
                    for (int i = 0; i < carriageCount; i++) {
                        addCarriageToLocomotive(newId, new Carriage(getWorld()));
                    }
                    JOptionPane.showMessageDialog(this, "Train Created Successfully");
                    System.out.println("Train Created");
                    break;

                // Requirement 4
                // Save commands
                case "save":
                    saveCommands();
                    break;

                // Requirement 4
                // Load commands
                case "load":
                    loadCommands();
                    break;

                // Requirement 7
                // HELP COMMAND
                case "help":
                    JOptionPane.showMessageDialog(
                            this,
                            "AVAILABLE COMMANDS:\n\n" +
                                    "about | start | stop | reset\n" +
                                    "addloco id x y | train id carriages x y\n" +
                                    "attachcarriage id | detachcarriage id\n" +
                                    "speed id speed | addjunction id x y | setjunction id val\n" +
                                    "save | load | status | emergency | exit | stats"
                    );
                    break;

                // Requirement 7
                // STATUS COMMAND
                case "status":
                    JOptionPane.showMessageDialog(this, "Railway Simulator Running Successfully");
                    break;

                // NEW: STATS COMMAND
                case "stats":
                    showStats();
                    break;

                // Requirement 7
                // EMERGENCY STOP
                case "emergency":
                    stopSimulation();
                    JOptionPane.showMessageDialog(this, "EMERGENCY STOP ACTIVATED");
                    break;

                // Extra feature
                // Exit program
                case "exit":
                    System.exit(0);
                    break;

                // Requirement 3
                default:
                    System.out.println("Invalid Command");
            }
        }

        // Requirement 3
        // Detect non-numeric values
        catch(NumberFormatException e)
        {
            System.out.println("Numbers Required");
        }

        // Requirement 3
        // Handle simulator exceptions
        catch(Exception e)
        {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Requirement 4
    // Save commands to file
    public void saveCommands()
    {
        JFileChooser chooser = new JFileChooser();

        if(chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
        {
            try
            {
                FileWriter fw = new FileWriter(chooser.getSelectedFile());

                // Write commands
                for(String cmd : commandHistory)
                {
                    fw.write(cmd + "\n");
                }

                fw.close();

                JOptionPane.showMessageDialog(this, "Commands Saved");
            }

            catch(Exception e)
            {
                System.out.println("Save Error");
            }
        }
    }

    // Requirement 4
    // Load commands from file
    public void loadCommands()
    {
        JFileChooser chooser = new JFileChooser();

        if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
        {
            try
            {
                Scanner sc = new Scanner(chooser.getSelectedFile());

                while(sc.hasNextLine())
                {
                    String cmd = sc.nextLine();

                    // Show loaded command
                    System.out.println("Loaded: " + cmd);

                    // Execute loaded command
                    executeCommand(cmd);
                }

                sc.close();

                JOptionPane.showMessageDialog(this, "Commands Loaded");
            }

            catch(Exception e)
            {
                System.out.println("Load Error");
            }
        }
    }

    // NEW: STATS DASHBOARD
    public void showStats() {
        String stats = String.format(
                "🚂 RAILWAY STATS DASHBOARD 🚂\n" +
                        "============================\n" +
                        "Total Locomotives Created: %d\n" +
                        "Active Locomotives: %d\n" +
                        "Total Commands Executed: %d\n" +
                        "Commands in History: %d\n" +
                        "============================",
                totalLocomotives,
                activeLocoIds.size(),
                totalCommands,
                commandHistory.size()
        );
        JOptionPane.showMessageDialog(this, stats, "Statistics", JOptionPane.INFORMATION_MESSAGE);
    }
}