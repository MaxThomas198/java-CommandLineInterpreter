import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.Scanner;

//Terminal class .. holds all commands implementation
class Terminal {

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
                    System.out.println("Folder deleted successfully");
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

    public void cat(String[] paths) throws IOException { //multitask function to read, write and interact with files

        if (paths[0] != null && !paths[0].equals(">")  &&  paths[1] == null && paths[2] == null) { //case 1 : read from file ex : cat test1

            String fileName;
            fileName = pathNameGenerator(paths[0]);  //generating the full path

            File file = new File(fileName);
            if (file.exists()) { //check if the file is exists

                try {
                    Scanner fileReader = new Scanner(file); // declare a reader
                    int i =0;
                    while (fileReader.hasNextLine()){

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

    public String pwd() {
        return GlobalConstants.currentPath;
    }

    public void clear() { //function for clearing the command window
        for (int i = 0 ; i < 800 ; i++)
            System.out.println();
        System.out.flush();
    }

    public String date() { //function to get the current date
        Date currentDate = new Date();
        return "Current date is " + currentDate;
    }

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

    public void pipe(String[] args) throws IOException {

        String firstCommand = args[0]; //the first command "cat"
        String secondCommand = args[3]; //the second command "more"

        switch (firstCommand){
            case "ls" :
                for (int i = 0; i < ls().length; i++) {
                    System.out.println(ls()[i]);
                }
                break;
            case "pwd" :
                System.out.println(pwd());
                break;
            case "mkdir" :
                mkdir(args[1]);
                break;
            case "rmdir":
                rmdir(args[1]);
                break;
            case "cp":
                cp(args[1], args[2]);
                break;
            case "mv":
                mv(args[1], args[2]);
                break;
            case "date":
                System.out.println(date());
                break;
            case "cd":
                cd(args[1]);
                break;
            case "clear":
                clear();
                break;
            case "help":
                for (int i = 0; i < help().length; i++) {
                    System.out.println(help()[i]);
                }
                break;
            case "cat":
                String [] catArgs = new String[3];
                try {
                    if(args[1] != null){ //the command name
                        catArgs[0] = args[0];
                    }
                    if (args[2] != null){ //first command arg 1
                        catArgs[1] = args[1];
                    }
                }catch (Exception ex){
                    //Exception
                }
                cat(catArgs);
                break;
            case "rm":
                String [] rmArgs = new String[3];
                try {
                    if(args[1] != null){ //the command name
                        rmArgs[0] = args[1];
                    }
                    if (args[2] != null){ //first command arg 1
                        rmArgs[1] = args[2];
                    }
                }catch (Exception ex){
                    //Exception
                }
                rm(rmArgs);
                break;
            default:
        } //switch for the first command

        switch (secondCommand){
            case "ls" :
                for (int i = 0; i < ls().length; i++) {
                    System.out.println(ls()[i]);
                }
                break;
            case "pwd" :
                System.out.println(pwd());
                break;
            case "mkdir" :
                mkdir(args[4]);
                break;
            case "rmdir":
                rmdir(args[4]);
                break;
            case "cp":
                cp(args[4], args[5]);
                break;
            case "mv":
                mv(args[4], args[5]);
                break;
            case "date":
                System.out.println(date());
                break;
            case "cd":
                cd(args[4]);
                break;
            case "clear":
                clear();
                break;
            case "help":
                for (int i = 0; i < help().length; i++) {
                    System.out.println(help()[i]);
                }
                break;
            case "cat":
                String [] catArgs = new String[3];
                try {
                    if(args[4] != null){ //the command name
                        catArgs[0] = args[4];
                    }
                    if (args[5] != null){ //first command arg 1
                        catArgs[1] = args[5];
                    }
                }catch (Exception ex){
                    //Exception
                }
                cat(catArgs);
                break;
            case "rm":
                String [] rmArgs = new String[3];
                try {
                    if(args[4] != null){ //the command name
                        rmArgs[0] = args[4];
                    }
                    if (args[5] != null){ //first command arg 1
                        rmArgs[1] = args[5];
                    }
                }catch (Exception ex){
                    //Exception
                }
                rm(rmArgs);
                break;
            default:
        } //switch for the second command

    }

}
