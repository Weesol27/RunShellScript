import java.io.*;
import java.util.Scanner;

public class RunShellScript {
    private static final String CONFIG_FILE = "config.txt";

    public static void main(String[] args) {
        try {
            File configFile = new File(CONFIG_FILE);
            String selectedScript = null;

            // Check if the config file exists
            if (configFile.exists()) {
                // Read the script name from the config file
                BufferedReader configReader = new BufferedReader(new FileReader(configFile));
                selectedScript = configReader.readLine();
                configReader.close();
                System.out.println("Using previously selected script: " + selectedScript);
                System.out.println("If you want to choose another script, delete the config file: " + CONFIG_FILE);
            } else {
                // Get the current directory
                File currentDir = new File(".");

                // List all .sh files in the current directory
                File[] files = currentDir.listFiles((dir, name) -> name.endsWith(".sh"));
                if (files == null || files.length == 0) {
                    System.out.println("No .sh files found in the current directory.");
                    return;
                } else if (files.length == 1) {
                    // Automatically select the only .sh file
                    selectedScript = files[0].getName();
                    System.out.println("Found only one .sh file, automatically selecting: " + selectedScript);
                } else {
                    // Display the .sh files and ask the user to choose one
                    System.out.println("Found the following .sh files:");
                    for (int i = 0; i < files.length; i++) {
                        System.out.println((i + 1) + ": " + files[i].getName());
                    }

                    Scanner scanner = new Scanner(System.in);
                    System.out.print("Enter the number of the script you want to run: ");
                    int choice = scanner.nextInt();
                    scanner.close();

                    if (choice < 1 || choice > files.length) {
                        System.out.println("Invalid choice.");
                        return;
                    }

                    selectedScript = files[choice - 1].getName();
                    System.out.println("Running script: " + selectedScript);
                }

                // Save the choice to the config file
                BufferedWriter configWriter = new BufferedWriter(new FileWriter(configFile));
                configWriter.write(selectedScript);
                configWriter.close();
            }

            // Execute the selected script
            String[] cmd = { "/bin/sh", selectedScript };
            Process process = Runtime.getRuntime().exec(cmd);

            // Read the output from the command
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // Wait for the process to complete
            int exitCode = process.waitFor();
            System.out.println("Exited with code: " + exitCode);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
