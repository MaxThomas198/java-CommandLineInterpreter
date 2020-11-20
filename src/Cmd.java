import java.io.IOException; //Exceptions
import java.util.Scanner; //User input

//main class
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

