import javax.swing.*;
import java.io.FileNotFoundException;
import java.io.IOException; //Exceptions
import java.nio.file.Path; //File Paths
import java.util.Arrays;
import java.util.Date; //Current Date
import java.util.Scanner; //User input
import java.io.File; //Files
import java.nio.file.Files; //Paths
import java.io.FileWriter;   // Import the FileWriter class


class GlobalConstants
{
    public static String currentPath = "/home/tony/"; //the default path
}

//Parser class .. validation for command , return the main command with it's arguments if it has any
class Parser {

    String[] args = new String[10]; //will be filled by arguments extracted by parse method
    String cmd; //will be filled by the command extracted by parse method

    public boolean parse(String input) {

        if (input.contains(" ")) {

            String command = input.substring(0, input.indexOf(" ")); //getting the first word

            if( input.contains(">") || input.contains(">>") || input.contains("|")){ //case :check if the input have redirect or pipe operator

                if(input.contains("|")){

                    String firstCommand = input.substring(0,input.indexOf("|")); //getting the first part
                    String[] firstCommandArgs = firstCommand.split(" "); //pushing in string array

                    try {
                        if(firstCommandArgs[0] != null){ //the command name
                            args[0] = firstCommandArgs[0];
                        }
                        if (firstCommandArgs[1] != null){ //first command arg 1
                            args[1] = firstCommandArgs[1];
                        }
                        if (firstCommandArgs[2] !=null){ //first command arg 2
                            args[2] = firstCommandArgs[2];
                        }
                    }catch (Exception ex){
                        //Exception
                    }

                    String secondCommand = input.substring(input.indexOf("|"), input.length()); //getting the first part
                    String[] secondCommandArgs = secondCommand.split(" "); //pushing in string array

                    try {
                        if(secondCommandArgs[1] != null){ //the command name
                            args[3] = secondCommandArgs[1];
                        }
                        if (secondCommandArgs[2] != null){ //first command arg 1
                            args[4] = secondCommandArgs[2];
                        }
                        if (secondCommandArgs[3] !=null){ //first command arg 2
                            args[5] = secondCommandArgs[3];
                        }
                    }catch (Exception ex){
                        //Exception
                    }

                    cmd = "|";
                    return true;

                }else if((input.contains(">") || input.contains(">>")) && !command.equals("cat") && ( command.equals("help")  || command.equals("ls") || command.equals("pwd") || command.equals("date") || command.equals("more"))) {

                    String[] arguments = input.split(" "); //getting the second word which is the argument

                    args[0] = command; //the first command
                    cmd = arguments[1]; // ">, >>"
                    args[1] = arguments[2]; //the second command

                }

                return true;

            } else if (command.equals("mkdir") || command.equals("rmdir") || command.equals("rm") || command.equals("args") || command.equals("cat") || command.equals("cd") || command.equals("cp") || command.equals("mv") || command.equals("more")) {

                String[] arguments = input.split(" "); //getting the arguments

                for (int i = 0; i < arguments.length; i++) {

                    try {
                        args[i] = arguments[i + 1];
                    } catch (Exception ex) {
                        args[i] = null;
                    }

                }

                cmd = command;

                return true;
            } else {
                return false;
            }
        }else {
            if (input.equals("help") || input.equals("ls") || input.equals("pwd") || input.equals("date") || input.equals("exit") || input.equals("clear")){ //case : commands that don't have arguments

                cmd = input;
                return true;

            }else {
                return false;
            }
        }
    }

    public String getCmd(){
        return cmd;
    }

    public String[] getArgs(){
        return args;
    }

} //tony

class Terminal {

    //tony
    public String pathNameGenerator(String path) { //function to generate the full given path if isn't correct

        if(path != null){
            if (!path.startsWith("/")) {

                if (!GlobalConstants.currentPath.endsWith("/")) {
                    return GlobalConstants.currentPath + "/" + path;
                } else {
                    return GlobalConstants.currentPath + path;
                }

            } else {
                return path;
            }
        }
        return null;
    }

    //tony
    public String returnLastPath(String path){ //function to get the last name in path ex : /tony/home/test --- > 'test'

        int numOfSlashes = path.length() - path.replace("/", "").length(); //count the number of slashes

        if(numOfSlashes == 0){
            return path;
        }else {
            String[] parts = path.split("/", numOfSlashes+1);

            int len = parts.length;
            String copyFileName = parts[len-1]; //return the word after the last slash

            return copyFileName;

        }
    }

    //tony
    public void rm(String[] paths){ //function to delete empty directories & files & non empty directories

        if (paths[0] != null && paths[1] == null){ //if the input is a file

            String finalPath;
            finalPath = pathNameGenerator(paths[0]);  //generating the full path

            File fileToDelete = new File(finalPath);

            if(fileToDelete.exists()){
                if(fileToDelete.isFile()){ // check if it's a file
                    fileToDelete.delete();
                    System.out.println("File deleted successfully");
                }else {
                    rmdir(paths[0]);
                }
            }else {
                System.out.println("File is not exists");
            }

        }else if(paths[0] != null && paths[0].equals("-R") && paths[1] != null){ // if the input has the argument '-R' which mean that the input is directory

            String finalPath;
            finalPath = pathNameGenerator(paths[1]); //generating the full path

            File folderToDelete = new File(finalPath);

            if(folderToDelete.exists()){

                File[] dirFiles = folderToDelete.listFiles(); //get all dir files

                if(dirFiles.length == 0){ //if the dir is empty
                    folderToDelete.delete();
                }else {
                    for(int i=0 ; i< dirFiles.length ; i++){
                        dirFiles[i].delete(); // deleting each file in the dir
                    }
                    folderToDelete.delete(); //then delete the parent directory
                    System.out.println("Folder deleted successfully");
                }

            }else {
                System.out.println("Folder is not exists");
            }

        }else{
            System.out.println("Invalid Arguments");
        }

    }

    //tony
    public void cat(String[] paths) throws IOException { //multitask function to read, write and interact with files

        if (paths[0] != null && !paths[0].equals(">")  &&  paths[1] == null && paths[2] == null) { //case 1 : read from file ex : cat test1

            String fileName;
            fileName = pathNameGenerator(paths[0]);  //generating the full path

            File file = new File(fileName);
            if (file.exists()) { //check if the file is exists

                try {
                    Scanner fileReader = new Scanner(file); // declare a reader
                    int i =0;
                    while (fileReader.hasNextLine() && i < 10){

                        String data = fileReader.nextLine(); //read the data while there any ..
                        System.out.println(data);
                        i++;
                    }

                    fileReader.close(); //closing the file after finish printing all the data
                } catch (FileNotFoundException ex) {
                    System.out.println("An error occurred while opening the file ");
                }

            } else {
                System.out.println("File not exists");
            }
        } else if (paths[0] != null && !paths[0].equals(">") && paths[1] != null  && !paths[1].equals(">") && !paths[1].equals(">>") && paths[2] == null) { //case 2 : read from two files ex : cat test1 test2

            String file1, file2;

            file1 = pathNameGenerator(paths[0]); //generating the full path
            file2 = pathNameGenerator(paths[1]); //generating the full path

            File firstFile = new File(file1);
            File secondFile = new File(file2);

            if (firstFile.exists() && secondFile.exists()) {

                try {
                    Scanner fileReader1 = new Scanner(firstFile); // declare a reader
                    Scanner fileReader2 = new Scanner(secondFile); // declare a reader
                    while (fileReader1.hasNextLine()) {
                        String data = fileReader1.nextLine();
                        System.out.println(data); //read the data while there any ..
                    }
                    while (fileReader2.hasNextLine()) {
                        String data = fileReader2.nextLine();
                        System.out.println(data); //read the data while there any ..
                    }
                    fileReader1.close(); //closing the file after finish printing all the data
                    fileReader2.close(); //closing the file after finish printing all the data

                } catch (Exception ex) {
                    System.out.println("An error occurred while opening the files ");
                }

            } else {
                System.out.println("File not exists");
            }

        } else if (paths[0] != null && paths[0].equals(">") && paths[1] != null && !paths[1].equals(">") && !paths[1].equals(">>") && paths[2] == null) { //case 3 : creating new file ex : cat > file1

            String file;

            file = pathNameGenerator(paths[1]);  //generating the full path

            try {
                File newFile = new File(file);
                newFile.createNewFile(); //creating new file with the given name
                System.out.println("File Created Successfully");

            } catch (Exception ex) {
                System.out.println("An error occurred while creating the files");
            }

        } else if (paths[0] != null && paths[1] != null && (paths[1].equals(">") || paths[1].equals(">>")) && paths[2] != null) { //case 4 : copying or appending content of two given files  ex : cat test1 >> test2

            String file1, file2;

            file1 = pathNameGenerator(paths[0]); //generating the full path
            file2 = pathNameGenerator(paths[2]); //generating the full path


            File firstFile = new File(file1);
            File secondFile = new File(file2);

            if(!secondFile.exists()){
                secondFile.createNewFile(); //creating the second file if it not found
            }

            if (firstFile.exists()) {

                try {
                    Scanner fileReader = new Scanner(firstFile); //making a reader to read the first file
                    FileWriter fileWriter; //and a writer to write in the second file

                    if(paths[1].equals(">")){ //if > means just copy
                        fileWriter = new FileWriter(secondFile);
                    }else { //if >> means append
                        fileWriter = new FileWriter(secondFile, true);
                    }

                    while (fileReader.hasNextLine()) {
                        String data = fileReader.nextLine(); //reading the data from the first file
                        fileWriter.write(data); // and then write it in the second file
                        fileWriter.write("\n");
                    }
                    fileReader.close(); //closing the file after finish reading all the data
                    fileWriter.close(); //closing the file after finish writing all the data

                    if(paths[1].equals(">")){
                        System.out.println("The content copied successfully ");
                    }else {
                        System.out.println("The content appended successfully ");
                    }


                } catch (Exception ex) {
                    System.out.println("An error occurred while opening the files ");
                }

            } else {
                System.out.println("File not exists");
            }

        }else {
            System.out.println("Invalid Arguments");
        }

    }

    //tony
    public void cp(String sourcePath, String destinationPath) throws IOException { // function to copy files , empty dir and non empty dir

        String file1, file2;

        file1 = pathNameGenerator(sourcePath); //generating the full path
        File fileToCopy = new File(file1);

        if (!fileToCopy.exists()){
            System.out.println("File is not exists");
        }

        file2 = pathNameGenerator(destinationPath); //generating the full path
        File fileToReceive = new File(file2);

        if(fileToCopy.isFile()){ // case 1 : if the the given destination path is to a file
            if (fileToReceive.isDirectory()){
                if (file2.endsWith("/")){

                    String finalName = file2 + returnLastPath(sourcePath); //generating the copied file name

                    File fileToWrite = new File(finalName); //creating the clone file with the same name in the destination dir
                    fileToWrite.createNewFile();

                    try {

                        Scanner fileReader = new Scanner(fileToCopy); //making a reader to read the first file
                        FileWriter fileWriter = new FileWriter(fileToWrite); //and a writer to write in the second file

                        while (fileReader.hasNextLine()) {
                            String data = fileReader.nextLine(); //reading the data from the first file
                            fileWriter.write(data); //and then write it in the second file
                            fileWriter.write("\n");
                        }

                        fileReader.close(); //closing the file after finish reading all the data
                        fileWriter.close(); //closing the file after finish writing all the data

                        System.out.println("File Copied Successfully");

                    }catch (Exception ex){
                        System.out.println("File Copied Failed");
                    }
                }else {
                    System.out.println("The destination path must be directory ");
                }
            }

        }else if(fileToCopy.isDirectory()){ //case 2 :  if the the given destination path is to a dir
            if (fileToReceive.isDirectory()) {
                if (file2.endsWith("/")) {

                    String dirName; //generating the copied file name

                    if(returnLastPath(sourcePath).equals(returnLastPath(file2))){
                        dirName = file2;
                    }else {
                        dirName = file2 + returnLastPath(sourcePath);
                    }

                    File[] dirFiles = fileToCopy.listFiles(); //dir files

                    if (dirFiles.length == 0) { //if the dir is empty

                        mkdir(dirName); //creating the dir by mkdir function
                        System.out.println("Directory Copied Successfully");

                    } else if(dirFiles.length != 0) { //if the dir is not empty

                        mkdir(dirName);

                        for(int i = 0 ; i < dirFiles.length ; i++){

                            if(dirFiles[i].isFile()){
                                File dirFile = new File(dirName+"/"+dirFiles[i].getName());
                                dirFile.createNewFile();

                                try {
                                    Scanner fileReader = new Scanner(dirFiles[i]); //making a reader to read the first file
                                    FileWriter fileWriter = new FileWriter(dirFile); //and a writer to write in the second file

                                    while (fileReader.hasNextLine()) {
                                        String data = fileReader.nextLine(); //reading the data from the first file
                                        fileWriter.write(data); //and then write it in the second file
                                        fileWriter.write("\n");
                                    }

                                    fileReader.close(); //closing the file after finish reading all the data
                                    fileWriter.close(); //closing the file after finish writing all the data

                                }catch (Exception e){
                                    //
                                }

                            }else if(dirFiles[i].isDirectory()){ //if the file is dir

                                String subDer = dirName+"/"; //generating the full path of sub dir

                                mkdir(subDer);

                                cp(dirFiles[i].getPath(), subDer); //recalling the function

                            }

                        }

                        System.out.println("Directory Copied Successfully");
                    }
                }
            }
        }


    }

    //tony
    public void mv(String sourcePath, String destinationPath) throws IOException { //same as cp function + deleting the old files at the end

        String file1, file2;

        file1 = pathNameGenerator(sourcePath); //generating the full path
        File fileToCopy = new File(file1);

        if (!fileToCopy.exists()){
            System.out.println("File is not exists");
        }

        file2 = pathNameGenerator(destinationPath); //generating the full path
        File fileToReceive = new File(file2);

        if(fileToCopy.isFile()){ // case 1 : if the the given destination path is to a file
            if (fileToReceive.isDirectory()){
                if (file2.endsWith("/")){

                    String finalName = file2 + returnLastPath(sourcePath); //generating the copied file name

                    File fileToWrite = new File(finalName); //creating the clone file with the same name in the destination dir
                    fileToWrite.createNewFile();

                    try {

                        Scanner fileReader = new Scanner(fileToCopy); //making a reader to read the first file
                        FileWriter fileWriter = new FileWriter(fileToWrite); //and a writer to write in the second file

                        while (fileReader.hasNextLine()) {
                            String data = fileReader.nextLine(); //reading the data from the first file
                            fileWriter.write(data); //and then write it in the second file
                            fileWriter.write("\n");
                        }

                        fileReader.close(); //closing the file after finish reading all the data
                        fileWriter.close(); //closing the file after finish writing all the data

                        System.out.println("File Copied Successfully");

                        String[] args = new String[1];
                        args[0] = sourcePath;
                        rm(args); //deleting the old file

                    }catch (Exception ex){
                        System.out.println("File Copied Failed");
                    }
                }else {
                    System.out.println("The destination path must be directory ");
                }
            }

        }else if(fileToCopy.isDirectory()){ //case 2 :  if the the given destination path is to a dir
            if (fileToReceive.isDirectory()) {
                if (file2.endsWith("/")) {

                    String dirName; //generating the copied file name

                    if(returnLastPath(sourcePath).equals(returnLastPath(file2))){
                        dirName = file2;
                    }else {
                        dirName = file2 + returnLastPath(sourcePath);
                    }

                    File[] dirFiles = fileToCopy.listFiles(); //dir files

                    if (dirFiles.length == 0) { //if the dir is empty

                        mkdir(dirName); //creating the dir by mkdir function
                        System.out.println("Directory Copied Successfully");

                        rmdir(sourcePath); //deleting the old empty dir


                    } else if(dirFiles.length != 0) { //if the dir is not empty

                        mkdir(dirName);

                        for(int i = 0 ; i < dirFiles.length ; i++){

                            if(dirFiles[i].isFile()){
                                File dirFile = new File(dirName+"/"+dirFiles[i].getName());
                                dirFile.createNewFile();

                                try {
                                    Scanner fileReader = new Scanner(dirFiles[i]); //making a reader to read the first file
                                    FileWriter fileWriter = new FileWriter(dirFile); //and a writer to write in the second file

                                    while (fileReader.hasNextLine()) {
                                        String data = fileReader.nextLine(); //reading the data from the first file
                                        fileWriter.write(data); //and then write it in the second file
                                        fileWriter.write("\n");
                                    }

                                    fileReader.close(); //closing the file after finish reading all the data
                                    fileWriter.close(); //closing the file after finish writing all the data


                                }catch (Exception e){
                                    //
                                }

                            }else if(dirFiles[i].isDirectory()){ //if the file is dir

                                String subDer = dirName+"/"; //generating the full path of sub dir

                                mkdir(subDer);

                                mv(dirFiles[i].getPath(), subDer); //recalling the function

                            }

                        }

                        String[] args = new String[2];
                        args[0] = "-R";
                        args[1] = sourcePath;
                        rm(args); //deleting the old non empty dir

                        System.out.println("Directory Copied Successfully");
                    }
                }
            }
        }


    }

    //tony
    public void cd(String destinationPath) { //function to change directory

        Path path = Path.of(pathNameGenerator(destinationPath));

        if (destinationPath.startsWith("/") && Files.exists(path)) {
            GlobalConstants.currentPath = destinationPath;
        }

        if (!destinationPath.startsWith("/") && Files.exists(path)) {

            Path pathAfter = Path.of(GlobalConstants.currentPath + destinationPath);
            if (Files.exists(pathAfter)) {
                GlobalConstants.currentPath += destinationPath;
            } else {
                System.out.println("cd : " + (GlobalConstants.currentPath + destinationPath) + " : no such file or directory ");
            }

        }

        if (destinationPath.startsWith(".")) {

            try {
                int numberOfPathsToBack = destinationPath.trim().split("/").length;

                for (int i = GlobalConstants.currentPath.length() - 1; i < numberOfPathsToBack; i--) {

                    if (GlobalConstants.currentPath.endsWith(".") || GlobalConstants.currentPath.endsWith("/")) {
                        GlobalConstants.currentPath.substring(0, GlobalConstants.currentPath.length() - 1);
                    }
                }
            } catch (Exception ex) {
                //exception here
            }

        }

    }

    //malek
    public String[] help() { //function for listing all the available commands

        String[] helps = new String[6];

        helps[0] = "clear ----> cleaning the command window";
        helps[1] = "cd ----> changing the directory, takes one parameter (the destination path)";
        helps[2] = "date ----> show the current time and date";
        helps[3] = "ls ----> list all the files and folders in the current path";
        helps[4] = "cp ----> copy file, takes two parameters (the source path, the destination path";
        helps[5] = "mkdir ----> create directory, takes one parameter (the destination path with the directory name added to it)";

        return helps;
    }

    //malek
    public void args(String commandName) { //function to list all commands arguments

        switch (commandName) {
            case "cp":
            case "mv":
                System.out.println("arg 1 : Source path , arg 2 : Destination path");
                break;
            case "cd":
                System.out.println("arg 1 : Destination path");
                break;
            case "mkdir":
                System.out.println("arg 1 : Destination path with new directory name stuck to it");
                break;
            case "rmdir":
            case "rm":
                System.out.println("arg 1 : Destination path or folder name");
                break;
            default:
                System.out.println(commandName + " : command not found");
                break;
        }
    }

    //malek
    public String pwd() {
        return GlobalConstants.currentPath;
    }

    //malek
    public void clear() { //function for clearing the command window
        for (int i = 0 ; i < 800 ; i++)
            System.out.println();
        System.out.flush();
    }

    //malek
    public String date() { //function to get the current date
        Date currentDate = new Date();
        return "Current date is " + currentDate;
    }

    //gomaa
    public void mkdir(String destinationPath) { //function to create directory

        String finalPath = pathNameGenerator(destinationPath);

        File newDir = new File(finalPath);

        if (!newDir.exists()) { // if the dir is already exists
            try {
                newDir.mkdirs(); // creating the dir
                System.out.println("Directory Created Successfully");

            } catch (Exception ex) {
                //
            }
        } else {
            System.out.println("This directory is already exists");
        }


    }

    //gomaa
    public void rmdir(String destinationPath) { //function to delete only empty directory

        String finalPath;

        finalPath = pathNameGenerator(destinationPath);  //generating the full path

        File destinationDir = new File(finalPath);
        File[] dirFiles = destinationDir.listFiles();

        if (dirFiles.length == 0) { // because rmdir only remove empty directories
            destinationDir.delete();
            System.out.println("Directory Deleted Successfully");

        } else {
            System.out.println("This directory cannot be deleted because it's not empty");
        }

    }

    //gomaa
    public String[] ls() { //function to list all the folders, files in the current path

        File currentDir = new File(GlobalConstants.currentPath);
        File[] dirFiles = currentDir.listFiles();
        String[] names = new String[dirFiles.length];

        try {
            if (dirFiles.length > 0 && dirFiles != null) {

                for (int i = 0; i < dirFiles.length; i++) {
//                    System.out.print(dirFiles[i].getName() + " "); //printing list of files of the given path
                    names[i] = dirFiles[i].getName()+ " ";
                }
                return names;
            }else {
                return null;
            }

        } catch (Exception e) {
            System.out.println("The is no files in this directory, please make sure that you type the path correctly");
            return null;
        }

    }

    //mohamed
    public void reDir(String[] args, String actionCommand) throws IOException {

        String firstCommand = args[0]; //the first command

        String secondCommand = args[1]; //the second command which is the file to write in it

        File file = new File(pathNameGenerator(secondCommand));
        if(!file.exists()){ //check if the file is exists
            file.createNewFile();
        }

        FileWriter fileWriter;
        if(actionCommand.equals(">>")){
            fileWriter = new FileWriter(file, true); //if the operator is >> then the operation will be appending text to file
        }else {
            fileWriter = new FileWriter(file); //if the operator is > then the operation will be copying text to file
        }


        switch (firstCommand){
            case "help":
                fileWriter.write("\n");
                for (int i = 0; i < help().length; i++){
                    fileWriter.write(help()[i]);
                    fileWriter.write("\n");
                }
                break;
            case "pwd":
                fileWriter.write("\n");
                fileWriter.write(pwd());
                break;

            case "date":
                fileWriter.write("\n");
                fileWriter.write(date());
                break;

            case "ls":
                fileWriter.write("\n");
                for (int i = 0; i < ls().length; i++){
                    fileWriter.write(ls()[i]);
                    fileWriter.write("\n");
                }
                break;

            default:
                break;
        }

        fileWriter.close();
        if(actionCommand.equals(">")){
            System.out.println("Content Copied to: " + file.getName() + " Successfully!");
        }else {
            System.out.println("Content appended to: " + file.getName() + " Successfully!");
        }


    }

    //mohamed
    public void more(String fileName) throws IOException{

        try {

            File myFile = new File(pathNameGenerator(fileName));
            Scanner myReader = new Scanner(myFile);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                System.out.println(data);
            }
            myReader.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("file not found.");
        }
    }

    //mohamed
    public void pipe(String[] args) {

        String firstCommand = args[0]; //the first command "cat"
        String secondCommand = args[3]; //the second command "more"

        if (firstCommand.equals("cat") && secondCommand.equals("more")){
            try {
                String fileName = args[1]; //which is the second argument : the file passed to cat function
                File file = new File(pathNameGenerator(fileName));
                if(file.exists()){
                    more(fileName); //displaying the whole data
                }else {
                    System.out.println("File not exists !");
                }

            }catch (Exception ex){
                //
            }

        }else {
            System.out.println("Invalid Commands");
        }


    }

}

//gomaa & malek & mohamed
    public class Cmd {

        public static void main(String[] args) throws IOException {

            Scanner input = new Scanner(System.in);

            String command;

            do {

                System.out.print("admin@root:" + GlobalConstants.currentPath + "$ "); //the text showing every time you complete a command
                command = input.nextLine();

                Parser parser = new Parser();
                if (parser.parse(command)) { //check if the parser function returns  true that means that the command is exists and valid

                    String actionCommand = parser.getCmd(); //return the actual command
                    String[] actionArgs = parser.getArgs(); //return the command arguments

                    Terminal action = new Terminal();

                    switch (actionCommand) {
                        case "help":
                            for (int i = 0; i < action.help().length; i++) {
                                System.out.println(action.help()[i]);
                            }
                            break;
                        case "pwd":
                            System.out.println(action.pwd());
                            break;
                        case "clear":
                            action.clear();
                            break;
                        case "args":
                            action.args(actionArgs[0]);
                            break;
                        case "date":
                            System.out.println(action.date());
                            break;
                        case "cd":
                            action.cd(actionArgs[0]);
                            break;
                        case "ls":
                            for (int i = 0; i < action.ls().length; i++) {
                                System.out.println(action.ls()[i]);
                            }
                            break;
                        case "mkdir":
                            action.mkdir(actionArgs[0]);
                            break;
                        case "rmdir":
                            action.rmdir(actionArgs[0]);
                            break;
                        case "rm":
                            action.rm(actionArgs);
                            break;
                        case "cp":
                            action.cp(actionArgs[0], actionArgs[1]);
                            break;
                        case "mv":
                            action.mv(actionArgs[0], actionArgs[1]);
                            break;
                        case "cat":
                            action.cat(actionArgs);
                            break;
                        case "more":
                            action.more(actionArgs[0]);
                            break;
                        case ">>":
                        case ">":
                            action.reDir(actionArgs, actionCommand);
                            break;
                        case "|":
                            action.pipe(actionArgs);
                            break;
                        default:
                    }

                } else {
                    if (command.equals("mkdir") || command.equals("rmdir") || command.equals("cat") || command.equals("rm") || command.equals("cd") || command.equals("cp") || command.equals("mv")) {
                        System.out.println(command + " : command arguments missing ");
                    } else {
                        System.out.println(command + " : command not found, please check that you type the command correctly and passed it's arguments if there any. ");
                    }
                }

            } while (!command.equals("exit"));


        }
    }

